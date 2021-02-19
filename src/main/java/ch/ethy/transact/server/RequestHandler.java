package ch.ethy.transact.server;

public interface RequestHandler {
  HttpResponse handle(HttpRequest request);
}
