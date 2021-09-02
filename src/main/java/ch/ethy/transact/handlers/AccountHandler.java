package ch.ethy.transact.handlers;

import ch.ethy.transact.account.*;
import ch.ethy.transact.server.*;

public class AccountHandler {

  private final AccountService accountService;

  public AccountHandler(AccountService accountService) {
    this.accountService = accountService;
  }

  public String register(HttpResponse httpResponse, UserDetails userInfo) {
    try {
      accountService.register(userInfo.username, userInfo.password);
      httpResponse.setStatus(201);
      httpResponse.setMessage("Created");
    } catch (UserAlreadyExistsException e) {
      httpResponse.setStatus(409);
      httpResponse.setMessage("Conflict");
      return "User already exists";
    }

    return null;
  }

  public static class UserDetails {
    private String username;
    private String password;

    @SuppressWarnings("unused")
    private UserDetails() {
      // for JSON parsing
    }

    public UserDetails(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }

  public static class RegistrationResponse {}
}
