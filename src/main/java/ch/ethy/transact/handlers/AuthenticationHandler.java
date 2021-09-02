package ch.ethy.transact.handlers;

import ch.ethy.transact.authentication.*;
import ch.ethy.transact.server.exception.*;

public class AuthenticationHandler {
  private final AuthenticationService authenticationService;

  public AuthenticationHandler(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  public LoginResponse login(Credentials credentials) {
    try {
      Token token = authenticationService.authenticate(credentials.username, credentials.password);
      return new LoginResponse(token.getToken(), token.getValidity());
    } catch (InvalidCredentialsException e) {
      throw new UnauthorizedException();
    }
  }

  public static class Credentials {
    private String username;
    private String password;

    @SuppressWarnings("unused")
    public Credentials() {
      // for JSON parsing
    }

    public Credentials(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }

  public static class LoginResponse {
    private final String token;
    private final long expires_in;

    public LoginResponse(String token, long expires_in) {
      this.token = token;
      this.expires_in = expires_in;
    }

    public String getToken() {
      return token;
    }

    public long getExpiresIn() {
      return expires_in;
    }
  }
}
