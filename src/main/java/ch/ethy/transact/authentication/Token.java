package ch.ethy.transact.authentication;

public class Token {
  private final String token;
  private final long validity;

  public Token(String token, long validity) {
    this.token = token;
    this.validity = validity;
  }

  public String getToken() {
    return token;
  }

  public long getValidity() {
    return validity;
  }
}
