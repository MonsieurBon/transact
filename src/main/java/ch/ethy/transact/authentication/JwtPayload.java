package ch.ethy.transact.authentication;

public class JwtPayload {
  private String jti;
  private Object sub;
  private long iat;
  private long exp;

  public static JwtPayload forSubject(Object subject) {
    JwtPayload payload = new JwtPayload();
    payload.sub = subject;
    return payload;
  }

  public JwtPayload withJwtId(String jwtId) {
    jti = jwtId;
    return this;
  }

  public JwtPayload issuedAt(long issuedAt) {
    iat = issuedAt;
    return this;
  }

  public JwtPayload expiringAt(long expirationTime) {
    this.exp = expirationTime;
    return this;
  }

  public String getJwtId() {
    return jti;
  }

  public Object getSubject() {
    return sub;
  }

  public long getIssuedAt() {
    return iat;
  }

  public long getExpirationTime() {
    return exp;
  }
}
