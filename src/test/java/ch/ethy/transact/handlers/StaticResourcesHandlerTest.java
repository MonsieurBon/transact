package ch.ethy.transact.handlers;

import ch.ethy.transact.server.*;
import ch.ethy.transact.server.exception.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static ch.ethy.transact.server.HttpHeader.*;
import static org.junit.jupiter.api.Assertions.*;

class StaticResourcesHandlerTest {
  @Test
  public void readsHtml() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.html", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    byte[] body = handler.handle(request, response);

    assertEquals("<html></html>", new String(body));
    assertEquals("text/html; charset=UTF-8", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readsCss() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.css", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    byte[] body = handler.handle(request, response);

    assertEquals("div {}", new String(body));
    assertEquals("text/css; charset=UTF-8", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readGif() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.gif", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    byte[] body = handler.handle(request, response);

    assertArrayEquals(new byte[]{71, 73, 70, 56, 57, 97, 1, 0, 1, 0, 0, -1, 0, 44, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 59}, body);
    assertEquals("image/gif", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readJpg() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.jpg", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    byte[] body = handler.handle(request, response);

    assertEquals(628, body.length);
    assertEquals("image/jpeg", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readJpeg() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.jpeg", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    byte[] body = handler.handle(request, response);

    assertEquals(628, body.length);
    assertEquals("image/jpeg", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void returns404IfFileNotFound() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/foo.html", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");
    StaticResourcesHandler handler = new StaticResourcesHandler("/dirDoesNotExist");

    assertThrows(NotFoundException.class, () -> handler.handle(request, response));
  }
}