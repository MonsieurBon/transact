package ch.ethy.transact.account;

import ch.ethy.transact.authentication.Base64;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

  private UserRepository repo;
  private AccountService service;

  @BeforeEach
  void setUp() {
    repo = new UserRepository();
    service = new AccountService(
        repo,
        () -> UUID.fromString("b366f5e7-8d00-4475-b022-3f1c765ebae1"),
        Base64::encode
    );
  }

  @Test
  public void registers_a_user() throws UserAlreadyExistsException {
    User user = service.register("username", "password");

    assertEquals(user, repo.get("b366f5e7-8d00-4475-b022-3f1c765ebae1"));
    assertEquals("b366f5e7-8d00-4475-b022-3f1c765ebae1", user.getId());
    assertEquals("username", user.getUsername());
    assertEquals("cGFzc3dvcmQ", user.getPassword());
  }

  @Test
  public void can_not_register_same_user_twice() throws UserAlreadyExistsException {
    service.register("username", "password");
    assertThrows(UserAlreadyExistsException.class, () -> service.register("username", "other_password"));
  }
}