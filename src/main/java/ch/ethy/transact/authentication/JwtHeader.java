package ch.ethy.transact.authentication;

public class JwtHeader {
  private String typ;
  private String alg;

  public static JwtHeader withAlgorithm(String algorithm) {
    JwtHeader jwtHeader = new JwtHeader();
    jwtHeader.typ = "JWT";
    jwtHeader.alg = algorithm;
    return jwtHeader;
  }

  public String getType() {
    return typ;
  }

  public String getAlgorithm() {
    return alg;
  }
}
