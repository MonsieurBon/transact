package ch.ethy.transact.security;

import ch.ethy.transact.json.parse.*;
import ch.ethy.transact.json.serialize.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.function.*;

import static ch.ethy.transact.authentication.Base64.*;

public class SecurityHelper {
  private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA512";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static Supplier<byte[]> randomBytes(int bytes) {
    return () -> {
      byte[] key = new byte[bytes];
      SECURE_RANDOM.nextBytes(key);
      return key;
    };
  }

  public static Function<String, String> passwordHasher(int iterations, int keyLength, int saltLength) {
    Supplier<byte[]> saltGenerator = randomBytes(saltLength);
    AlgorithmSpecs specs = new AlgorithmSpecs(HASH_ALGORITHM, iterations, keyLength);
    String specsJson = new JsonSerializer(specs).serialize();

    return (password) -> {
      try {
        byte[] salt = saltGenerator.get();

        byte[] hash = hashPassword(specs, password, salt);
        return String.format("%s.%s.%s", encode(specsJson), encode(salt), encode(hash));
      } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static BiPredicate<String, String> passwordVerifier() {
    return (hash, password) -> {
      try {
        String[] hashParts = hash.split("\\.");
        AlgorithmSpecs specs = new JsonParser(decodeToString(hashParts[0])).parse(AlgorithmSpecs.class);
        byte[] salt = decode(hashParts[1]);

        String passwordHash = encode(hashPassword(specs, password, salt));

        return passwordHash.equals(hashParts[2]);
      } catch(Exception e) {
        return false;
      }
    };
  }

  private static byte[] hashPassword(AlgorithmSpecs specs, String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, specs.iterations, specs.keyLength);
    SecretKeyFactory factory = SecretKeyFactory.getInstance(specs.algorithm);
    return factory.generateSecret(spec).getEncoded();
  }

  private static class AlgorithmSpecs {
    private String algorithm;
    private int iterations;
    private int keyLength;

    @SuppressWarnings("unused")
    public AlgorithmSpecs() {
      // used for JSON deserializing
    }

    public AlgorithmSpecs(String algorithm, int iterations, int keyLength) {
      this.algorithm = algorithm;
      this.iterations = iterations;
      this.keyLength = keyLength;
    }
  }
}
