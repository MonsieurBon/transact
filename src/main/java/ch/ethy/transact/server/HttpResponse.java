package ch.ethy.transact.server;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ch.ethy.transact.server.HttpHeader.CONTENT_LENGTH;

public class HttpResponse {
  private final String httpVersion;
  private final Map<String, String> headers = new HashMap<>();
  private int status;
  private String message;
  private byte[] body = {};

  public HttpResponse(String httpVersion, int status, String message) {
    this.httpVersion = httpVersion;
    this.status = status;
    this.message = message;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
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

  void setBody(byte[] body) {
    this.body = body;
    this.headers.put(CONTENT_LENGTH, String.valueOf(this.body.length));
  }

  void setBody(String body) {
    setBody(body.getBytes(StandardCharsets.UTF_8));
  }
}
