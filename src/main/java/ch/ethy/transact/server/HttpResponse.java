package ch.ethy.transact.server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ch.ethy.transact.server.HttpHeader.CONTENT_LENGTH;

public class HttpResponse {
  private final String httpVersion;
  private final int code;
  private final String message;
  private final Map<String, String> headers = new HashMap<>();
  private byte[] body = {};

  public HttpResponse(String httpVersion, int code, String message) {
    this.httpVersion = httpVersion;
    this.code = code;
    this.message = message;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void addHeader(String header, String value) {
    headers.put(header, value);
  }

  public byte[] getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body.getBytes(StandardCharsets.UTF_8);
    this.headers.put(CONTENT_LENGTH, String.valueOf(this.body.length));
  }
}
