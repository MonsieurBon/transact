package ch.ethy.transact.server;

import java.util.Objects;

public class HttpContext {
  private final String path;
  private final HttpMethod method;
  private final RequestHandler requestHandler;

  public HttpContext(String path, HttpMethod method, RequestHandler requestHandler) {
    this.path = path;
    this.method = method;
    this.requestHandler = requestHandler;
  }

  public String getPath() {
    return path;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public RequestHandler getHandler() {
    return requestHandler;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HttpContext that = (HttpContext) o;
    return Objects.equals(path, that.path) && method == that.method;
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, method);
  }
}
