package ch.ethy.transact.server;

public interface RequestHandler {
  Object handle(HttpRequest request, HttpResponse response);
}
