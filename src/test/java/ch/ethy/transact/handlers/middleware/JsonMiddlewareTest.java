package ch.ethy.transact.handlers.middleware;

import ch.ethy.transact.json.serialize.*;
import ch.ethy.transact.server.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;
import java.util.*;

import static ch.ethy.transact.handlers.middleware.JsonMiddleware.*;
import static ch.ethy.transact.server.HttpMethod.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonMiddlewareTest {
  @Test
  public void asJsonReturnsSerializedBody() {
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");

    RequestHandler asJsonHandler = asJson((req, res) -> List.of("a", "b"));

    Object body = asJsonHandler.handle(null, response);
    assertEquals("[\"a\",\"b\"]", body);
    assertEquals("application/json; charset=UTF-8", response.getHeaders().get("Content-Type"));
  }

  @Test
  public void fromJsonPassesParsedBodyToHandler() {
    TestObject testObject = new TestObject();
    testObject.stringProp = "someString";

    String body = new JsonSerializer(testObject).serialize();

    HttpRequest request = new HttpRequest(GET, "/", "HTTP/1.1", Collections.emptyMap(), body.getBytes(StandardCharsets.UTF_8));

    RequestHandler fromJsonHandler = fromJson((req, res, to) -> {
      assertEquals("someString", to.stringProp);
      return 42;
    }, TestObject.class);

    Object result = fromJsonHandler.handle(request, null);
    assertEquals(42, result);
  }

  private static class TestObject {
    private String stringProp;
  }
}
