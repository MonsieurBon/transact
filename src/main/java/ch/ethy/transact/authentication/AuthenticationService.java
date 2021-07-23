package ch.ethy.transact.authentication;

import ch.ethy.transact.json.parse.*;
import ch.ethy.transact.json.serialize.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.*;
import java.security.*;
import java.security.spec.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import static ch.ethy.transact.authentication.Base64.*;

public class AuthenticationService {
  private static final List<AlgorithmsMapping> JWT_JAVA_ALGS = List.of(
      new AlgorithmsMapping("HS256", "HmacSHA256")
  );

  private final Map<String, Mac> macs = new HashMap<>();

  private final DateTimeProvider dateTimeProvider;
  private final UUIDProvider uuidProvider;
  private final UserProvider userProvider;

  private Set<TokenRevokation> revokedTokens = new HashSet<>();

  public AuthenticationService(
      DateTimeProvider dateTimeProvider,
      UUIDProvider uuidProvider,
      UserProvider userProvider,
      String key
  ) {
    this.dateTimeProvider = dateTimeProvider;
    this.uuidProvider = uuidProvider;
    this.userProvider = userProvider;

    JWT_JAVA_ALGS.forEach(alg -> macs.put(alg.java, createMac(alg.java, key)));
  }

  private Mac createMac(String algorithm, String key) {
    try {
      Mac mac = Mac.getInstance(algorithm);
      SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
      mac.init(secret_key);
      return mac;
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  public String authenticate(String username, String password) {
    SecurityUser user = userProvider.getByUsername(username);

    if (user != null && passwordIsValid(user, password)) {
      return createToken(user.getId());
    }

    throw new InvalidCredentialsException();
  }

  public boolean verifyToken(String token) {
    String[] jwtParts = token.split("\\.", 3);

    if (jwtParts.length < 3) {
      return false;
    }

    String javaAlg = getAlgorithm(jwtParts[0]);

    String signature = calculateSignature(javaAlg, jwtParts[0], jwtParts[1]);
    if (!signature.equals(jwtParts[2])) {
      return false;
    }

    JwtPayload payload = parsePayload(jwtParts[1]);

    long expirationTime = payload.getExpirationTime();
    long now = dateTimeProvider.now().toEpochSecond();

    if (expirationTime < now) {
      return false;
    }

    return revokedTokens.stream()
        .noneMatch(revokation -> revokation.jwtId.equals(payload.getJwtId()));
  }

  public void revoke(String token) {
    String[] jwtParts = token.split("\\.", 3);

    JwtPayload payload = new JsonParser(decodeToString(jwtParts[1])).parse(JwtPayload.class);
    revokedTokens.add(new TokenRevokation(payload.getJwtId(), payload.getExpirationTime()));

    cleanupRevokations();
  }

  private void cleanupRevokations() {
    long now = dateTimeProvider.now().toEpochSecond();
    revokedTokens = revokedTokens.stream()
        .filter(revokation -> revokation.expirationTime > now)
        .collect(Collectors.toSet());
  }

  private JwtPayload parsePayload(String encodedPayload) {
    String jsonPayload = decodeToString(encodedPayload);
    return new JsonParser(jsonPayload).parse(JwtPayload.class);
  }

  private String getAlgorithm(String encodedHeader) {
    String jsonHeader = decodeToString(encodedHeader);
    JwtHeader header = new JsonParser(jsonHeader).parse(JwtHeader.class);
    return JWT_JAVA_ALGS.stream()
        .filter(alg -> alg.jwt.equals(header.getAlgorithm()))
        .findFirst()
        .map(alg -> alg.java)
        .orElse(JWT_JAVA_ALGS.get(0).java);
  }

  private boolean passwordIsValid(SecurityUser user, String password) {
    try {
      byte[] salt = decode(user.getSalt());

      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      String encodedPassword = encode(factory.generateSecret(spec).getEncoded());

      return user.getPassword().equals(encodedPassword);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private String createToken(Object subject) {
    String jwtAlg = JWT_JAVA_ALGS.get(0).jwt;
    String javaAlg = JWT_JAVA_ALGS.get(0).java;

    JwtHeader jwtHeader = JwtHeader.withAlgorithm(jwtAlg);
    String header = encode(new JsonSerializer(jwtHeader).serialize());

    ZonedDateTime issuedAt = dateTimeProvider.now();
    ZonedDateTime expirationTime = issuedAt.plusMinutes(20);
    JwtPayload jwtPayload = JwtPayload.forSubject(subject)
        .withJwtId(uuidProvider.getUuid().toString())
        .issuedAt(issuedAt.toEpochSecond())
        .expiringAt(expirationTime.toEpochSecond());
    String payload = encode(new JsonSerializer(jwtPayload).serialize());

    String signature = calculateSignature(javaAlg, header, payload);

    return String.format("%s.%s.%s", header, payload, signature);
  }

  private String calculateSignature(String algorithm, String header, String payload) {
    String message = String.join(".", header, payload);
    return encode(hash(algorithm, message));
  }

  private byte[] hash(String algorithm, String input) {
    return macs.get(algorithm).doFinal(input.getBytes(StandardCharsets.UTF_8));
  }

  private static class AlgorithmsMapping {
    private final String jwt;
    private final String java;

    public AlgorithmsMapping(String jwt, String java) {
      this.jwt = jwt;
      this.java = java;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AlgorithmsMapping that = (AlgorithmsMapping) o;
      return jwt.equals(that.jwt) && java.equals(that.java);
    }

    @Override
    public int hashCode() {
      return Objects.hash(jwt, java);
    }
  }

  private static class TokenRevokation {
    private final String jwtId;
    private final long expirationTime;

    public TokenRevokation(String jwtId, long expirationTime) {
      this.jwtId = jwtId;
      this.expirationTime = expirationTime;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TokenRevokation that = (TokenRevokation) o;
      return expirationTime == that.expirationTime && jwtId.equals(that.jwtId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(jwtId, expirationTime);
    }
  }
}
