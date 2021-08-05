package ch.ethy.transact.handlers.middleware;

import ch.ethy.transact.json.parse.*;
import ch.ethy.transact.json.serialize.*;
import ch.ethy.transact.server.*;

import static ch.ethy.transact.server.ContentType.*;
import static ch.ethy.transact.server.HttpHeader.*;

public class JsonMiddleware {
  public static RequestHandler asJson(RequestHandler nextHandler) {
    return (request, response) -> {
      Object body = nextHandler.handle(request, response);
      response.addHeader(CONTENT_TYPE, APPLICATION_JSON);
      return new JsonSerializer(body).serialize();
    };
  }

  public static <T> RequestHandler fromJson(JsonRequestHandler<T> nextHandler, Class<T> clazz) {
    return (request, response) -> {
      String body = new String(request.getBody());
      T jsonBody = new JsonParser(body).parse(clazz);
      return nextHandler.handle(request, response, jsonBody);
    };
  }

  public interface JsonRequestHandler<T> {
    Object handle(HttpRequest request, HttpResponse response, T body);
  }
}
