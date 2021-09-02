package ch.ethy.transact.handlers;

import ch.ethy.transact.account.*;
import ch.ethy.transact.handlers.AccountHandler.*;
import ch.ethy.transact.server.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AccountHandlerTest {
  private static final UserDetails USER_INFO = new UserDetails("username", "password");

  private AccountHandler handler;
  private MockAccountService accountService;

  @BeforeEach
  public void setUp() {
    accountService = new MockAccountService();
    handler = new AccountHandler(accountService);
  }

  @Test
  public void returns_201_on_registration() {
    HttpResponse httpResponse = new HttpResponse("HTTP/1.1", 200, "OK");
    handler.register(httpResponse, USER_INFO);

    assertEquals(201, httpResponse.getStatus());
    assertEquals("Created", httpResponse.getMessage());
    assertEquals("username", accountService.lastUsername);
    assertEquals("password", accountService.lastPassword);
  }

  @Test
  public void returns_409_on_duplicate_user() {
    HttpResponse httpResponse = new HttpResponse("HTTP/1.1", 200, "OK");
    handler.register(httpResponse, USER_INFO);
    String message = handler.register(httpResponse, USER_INFO);

    assertEquals(409, httpResponse.getStatus());
    assertEquals("Conflict", httpResponse.getMessage());
    assertEquals("User already exists", message);
  }

  private static class MockAccountService extends AccountService {
    private String lastUsername;
    private String lastPassword;

    public MockAccountService() {
      super(null, null, null);
    }

    @Override
    public User register(String username, String password) throws UserAlreadyExistsException {
      if (lastUsername != null && lastUsername.equals(username)) {
        throw new UserAlreadyExistsException();
      }

      lastUsername = username;
      lastPassword = password;
      return null;
    }
  }
}
