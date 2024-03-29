package ch.ethy.transact.authentication;

import ch.ethy.transact.json.parse.*;
import org.junit.jupiter.api.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;

import static ch.ethy.transact.authentication.AuthenticationServiceTest.MockUserBuilder.*;
import static ch.ethy.transact.authentication.Base64.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {
  private static final byte[] KEY = {-42, 0, 42};
  private AuthenticationService service;
  private Boolean isPasswordValid = true;

  @BeforeEach
  public void setup() {
    ZoneId CET = ZoneId.of("Europe/Zurich");
    final ZonedDateTime now = ZonedDateTime.of(2021, 6, 10, 21, 10, 15, 0, CET);

    MockUserProvider userProvider = new MockUserProvider();
    userProvider.users.put("username", aUser().build());

    service = new AuthenticationService(
        () -> now,
        () -> UUID.fromString("b366f5e7-8d00-4475-b022-3f1c765ebae1"),
        userProvider,
        (s, s2) -> isPasswordValid,
        KEY
    );
  }

  @Test
  public void authenticating_creates_valid_jwt_token() {
    Token jwt = service.authenticate("username", "password");

    String[] jwtParts = jwt.getToken().split("\\.", 3);
    String header = jwtParts[0];
    String payload = jwtParts[1];
    String signature = jwtParts[2];

    assertTrue(header.length() > 0);
    assertTrue(payload.length() > 0);
    assertTrue(signature.length() > 0);
    assertDoesNotThrow(() -> decodeToString(header));
    assertDoesNotThrow(() -> decodeToString(payload));
    assertDoesNotThrow(() -> decodeToString(signature));
  }

  @Test
  public void jwt_has_valid_signature() throws Exception {
    Token jwt = service.authenticate("username", "password");

    String[] jwtParts = jwt.getToken().split("\\.", 3);
    String header = jwtParts[0];
    String payload = jwtParts[1];
    String signature = jwtParts[2];

    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(KEY, "HmacSHA256");
    sha256_HMAC.init(secret_key);
    String input = String.format("%s.%s", header, payload);
    byte[] hash = sha256_HMAC.doFinal(input.getBytes(StandardCharsets.UTF_8));

    assertArrayEquals(hash, decode(signature));
  }

  @Test
  public void jwt_has_valid_header() {
    Token jwt = service.authenticate("username", "password");

    String[] jwtParts = jwt.getToken().split("\\.", 3);
    JwtHeader header = new JsonParser(decodeToString(jwtParts[0])).parse(JwtHeader.class);

    assertEquals("JWT", header.getType());
    assertEquals("HS256", header.getAlgorithm());
  }

  @Test
  public void jwt_has_valid_payload() {
    Token jwt = service.authenticate("username", "password");

    String[] jwtParts = jwt.getToken().split("\\.", 3);
    JwtPayload payload = new JsonParser(decodeToString(jwtParts[1])).parse(JwtPayload.class);

    assertEquals("username", payload.getSubject());
    assertEquals(1623352215L, payload.getIssuedAt());
    assertEquals(1623353415L, payload.getExpirationTime());
    assertEquals("b366f5e7-8d00-4475-b022-3f1c765ebae1", payload.getJwtId());
  }

  @Test
  public void token_expiration_is_returned_correctly() {
    Token jwt = service.authenticate("username", "password");
    long validity = jwt.getValidity();

    assertEquals(1200L, validity);
  }

  @Test
  public void authenticate_throws_invalid_credentials_for_bad_username() {
    assertThrows(InvalidCredentialsException.class, () -> service.authenticate("no_user", "password"));
  }

  @Test
  public void authenticate_throws_invalid_credentials_for_bad_password() {
    isPasswordValid = false;
    assertThrows(InvalidCredentialsException.class, () -> service.authenticate("username", "bad_password"));
  }

  @Test
  public void verifyToken_accepts_valid_token() {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzY2ZjVlNy04ZDAwLTQ0NzUtYjAyMi0zZjFjNzY1ZWJhZTEiLCJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYyMzM1MjIxNSwiZXhwIjoxNjIzMzUzNDE1fQ.4pHNIoiIORO1GieP1wQ7M6h7zDQ5z1JD53C9b-QuzsQ";
    assertTrue(service.verifyToken(token));
  }

  @Test
  public void verifyToken_rejects_invalid_format() {
    String notEnoughtParts = "h.p";
    assertFalse(service.verifyToken(notEnoughtParts));
  }

  @Test
  public void verifyToken_rejects_invalid_signature() {
    String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzY2ZjVlNy04ZDAwLTQ0NzUtYjAyMi0zZjFjNzY1ZWJhZTEiLCJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYyMzM1MjIxNSwiZXhwIjoxNjIzMzUzNDE1fQ.Q7uDtWoXhZpyKtwcWYzuPniOShmSyY2l14Bj64Vipe";
    assertFalse(service.verifyToken(invalidToken));
  }

  @Test
  public void verifyToken_rejects_expired_token() {
    String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzY2ZjVlNy04ZDAwLTQ0NzUtYjAyMi0zZjFjNzY1ZWJhZTEiLCJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYyMzI2NTgxNSwiZXhwIjoxNjIzMjY3MDE1fQ.nv27EJ8rQGtYexp8HdhqDfyumFbTeeH8q7pTyWPhAOk";
    assertFalse(service.verifyToken(expiredToken));
  }

  @Test
  public void verifyToken_rejects_revoked_tokens() {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzY2ZjVlNy04ZDAwLTQ0NzUtYjAyMi0zZjFjNzY1ZWJhZTEiLCJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYyMzM1MjIxNSwiZXhwIjoxNjIzMzUzNDE1fQ.Q7uDtWoXhZpyKtwcWYzuPniOShmSyY2l14Bj64Vipeo";
    service.revoke(token);
    assertFalse(service.verifyToken(token));
  }

  public static class MockUserProvider implements UserProvider {
    private final Map<String, MockUser> users = new HashMap<>();

    @Override
    public MockUser getByUsername(String username) {
      return users.get(username);
    }
  }

  public static class MockUser implements SecurityUser {
    private final String username;
    private final String password;

    public MockUser(String username, String password) {
      this.username = username;
      this.password = password;
    }

    @Override
    public String getId() {
      return username;
    }

    @Override
    public String getPassword() {
      return password;
    }
  }

  public static class MockUserBuilder {
    private String username = "username";
    private String password = "password_hash";

    public static MockUserBuilder aUser() {
      return new MockUserBuilder();
    }

    public MockUserBuilder setUsername(String username) {
      this.username = username;
      return this;
    }

    public MockUserBuilder setPassword(String password) {
      this.password = password;
      return this;
    }

    public AuthenticationServiceTest.MockUser build() {
      return new AuthenticationServiceTest.MockUser(username, password);
    }
  }
}
