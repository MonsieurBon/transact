package ch.ethy.transact.server;

import java.util.Map;

public class HttpRequest {
  private final HttpMethod method;
  private final String path;
  private final String httpVersion;
  private final Map<String, String> headers;
  private final byte[] body;

  public HttpRequest(HttpMethod method, String path, String httpVersion, Map<String, String> headers, byte[] body) {
    this.method = method;
    this.path = path;
    this.httpVersion = httpVersion;
    this.headers = headers;
    this.body = body;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public String getHeader(String name) {
    return headers.get(name);
  }

  public byte[] getBody() {
    return body;
  }
}
