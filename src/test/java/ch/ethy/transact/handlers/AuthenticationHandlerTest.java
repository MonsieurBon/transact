package ch.ethy.transact.handlers;

import ch.ethy.transact.authentication.*;
import ch.ethy.transact.handlers.AuthenticationHandler.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationHandlerTest {
  private static final Credentials VALID_CREDENTIALS = new Credentials("username", "password");
  private static final Credentials INVALID_CREDENTIALS = new Credentials("badusername", "badpassword");

  private AuthenticationHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new AuthenticationHandler(new MockAuthenticationService());
  }

  @Test
  public void handlesValidCredentials() {
    LoginResponse response = handler.login(VALID_CREDENTIALS);

    assertEquals("some-token", response.getToken());
    assertEquals(3600L, response.getExpiresIn());
  }

  @Test
  public void handlesInvalidCredentials() {
    LoginResponse response = handler.login(INVALID_CREDENTIALS);

    assertEquals("some-token", response.getToken());
    assertEquals(3600L, response.getExpiresIn());
  }

  private static class MockAuthenticationService extends AuthenticationService {
    public MockAuthenticationService() {
      super(() -> null, () -> null, username -> null, (s1, s2) -> true, new byte[0]);
    }

    @Override
    public Token authenticate(String username, String password) {
      if (username.equals("username")) {
        return new Token("some-token", 3600L);
      } else {
        throw new InvalidCredentialsException();
      }
    }
  }
}
