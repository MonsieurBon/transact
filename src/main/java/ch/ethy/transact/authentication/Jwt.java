package ch.ethy.transact.authentication;

import ch.ethy.transact.json.serialize.*;

import static ch.ethy.transact.authentication.Base64.*;

public class Jwt {
  private JwtHeader header;
  private JwtPayload payload;
  private String signature;

  @Override
  public String toString() {
    String header = encode(new JsonSerializer(this.header).serialize());
    String payload = encode(new JsonSerializer(this.payload).serialize());
    String signature = encode(this.signature);

    return String.format("%s.%s.%s", header, payload, signature);
  }
}
