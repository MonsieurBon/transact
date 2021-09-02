package ch.ethy.transact.server;

import ch.ethy.transact.server.exception.MethodNotAllowedException;

public enum HttpMethod {
  HEAD,
  GET,
  POST,
  PUT;

  public static HttpMethod get(String method) {
    try {
      return HttpMethod.valueOf(method);
    } catch (IllegalArgumentException e) {
      throw new MethodNotAllowedException();
    }
  }
}
