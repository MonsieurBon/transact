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
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    HttpResponse response = handler.handle(request);

    assertEquals("<html></html>", new String(response.getBody()));
    assertEquals("text/html; charset=UTF-8", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readsCss() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.css", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    HttpResponse response = handler.handle(request);

    assertEquals("div {}", new String(response.getBody()));
    assertEquals("text/css; charset=UTF-8", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readGif() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.gif", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    HttpResponse response = handler.handle(request);

    assertArrayEquals(new byte[]{71, 73, 70, 56, 57, 97, 1, 0, 1, 0, 0, -1, 0, 44, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 0, 59}, response.getBody());
    assertEquals("image/gif", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readJpg() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.jpg", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    HttpResponse response = handler.handle(request);

    assertEquals(628, response.getBody().length);
    assertEquals("image/jpeg", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void readJpeg() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/test.jpeg", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    StaticResourcesHandler handler = new StaticResourcesHandler("/javaUnitTest");
    HttpResponse response = handler.handle(request);

    assertEquals(628, response.getBody().length);
    assertEquals("image/jpeg", response.getHeaders().get(CONTENT_TYPE));
  }

  @Test
  public void returns404IfFileNotFound() {
    HttpRequest request = new HttpRequest(HttpMethod.GET, "/foo.html", "HTTP/1.1", Collections.emptyMap(), new byte[0]);
    StaticResourcesHandler handler = new StaticResourcesHandler("/dirDoesNotExist");

    assertThrows(NotFoundException.class, () -> handler.handle(request));
  }
}