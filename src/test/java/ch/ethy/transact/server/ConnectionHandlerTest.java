package ch.ethy.transact.server;

import ch.ethy.transact.server.exception.InternalServerError;
import ch.ethy.transact.server.exception.MethodNotAllowedException;
import ch.ethy.transact.server.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.ethy.transact.server.HttpMethod.GET;
import static ch.ethy.transact.server.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionHandlerTest {
  @Test
  public void callsHandler() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/");
    MockRequestHandler handler = new MockRequestHandler();
    HttpContext context = new HttpContext("/", GET, handler);
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(context);

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    assertTrue(handler.hasBeenCalled);
  }

  @Test
  public void callsHandlerForMatchingPath() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/foo");
    MockRequestHandler fooHandler = new MockRequestHandler();
    MockRequestHandler barHandler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/foo", GET, fooHandler));
    contexts.add(new HttpContext("/bar", GET, barHandler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    assertTrue(fooHandler.hasBeenCalled);
    assertFalse(barHandler.hasBeenCalled);
  }

  @Test
  public void callsHandlerForLongestSubpath() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/foo/bar/baz");
    MockRequestHandler fooHandler = new MockRequestHandler();
    MockRequestHandler fooBarHandler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/foo", GET, fooHandler));
    contexts.add(new HttpContext("/foo/bar", GET, fooBarHandler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    assertFalse(fooHandler.hasBeenCalled);
    assertTrue(fooBarHandler.hasBeenCalled);
  }

  @Test
  public void callsHandlerForMatchingMethod() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/foo");
    MockRequestHandler getHandler = new MockRequestHandler();
    MockRequestHandler postHandler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/foo", GET, getHandler));
    contexts.add(new HttpContext("/foo", POST, postHandler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    assertTrue(getHandler.hasBeenCalled);
    assertFalse(postHandler.hasBeenCalled);
  }

  @Test
  public void throws404NotFoundExceptionIfNoPathMatches() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/foo");
    MockRequestHandler barHandler = new MockRequestHandler();
    MockRequestHandler bazHandler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/bar", GET, barHandler));
    contexts.add(new HttpContext("/baz", GET, bazHandler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    assertThrows(NotFoundException.class, () -> connectionHandler.handle(connection));
  }

  @Test
  public void throws405MethodNotAllowedExceptionIfNoMethod() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/foo");
    MockRequestHandler postHandler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/foo", POST, postHandler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    assertThrows(MethodNotAllowedException.class, () -> connectionHandler.handle(connection));
  }

  @Test
  public void createsHttpRequestObjectAndPassesItToHandler() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/");
    MockRequestHandler handler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/", GET, handler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    HttpRequest request = handler.request;
    assertNotNull(request);
    assertEquals(GET, request.getMethod());
    assertEquals("/", request.getPath());
    assertEquals("HTTP/1.1", request.getHttpVersion());
    assertEquals("value1", request.getHeader("Header1"));
    assertEquals("value2", request.getHeader("Header2"));
    assertEquals("value3", request.getHeader("Header3"));
    assertEquals("Bodyline1\r\nBodyline2\r\nBodyline3\r\nBodyline4", new String(request.getBody()));
  }

  @Test
  public void writesHttpResponseToOutputStream() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/");
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/", GET, request -> {
      HttpResponse response = new HttpResponse("HTTP/1.1", 123, "Foobar");

      response.addHeader("Header1", "value1");
      response.addHeader("Header2", "value2");
      response.addHeader("Header3", "value3");

      response.setBody("Body");

      return response;
    }));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(connection);

    List<String> headerOutput = connection.getHeaderWriter().lines;
    assertEquals(6, headerOutput.size());
    assertEquals("HTTP/1.1 123 Foobar", headerOutput.get(0));
    assertEquals("Header1: value1", headerOutput.get(1));
    assertEquals("Header2: value2", headerOutput.get(2));
    assertEquals("Header3: value3", headerOutput.get(3));
    assertEquals("Content-Length: 4", headerOutput.get(4));
    assertEquals("", headerOutput.get(5));

    List<byte[]> bodyOutput = connection.getBodyOutputStream().bytes;
    assertEquals(1, bodyOutput.size());
    assertArrayEquals(new byte[] {'B', 'o', 'd', 'y'}, bodyOutput.get(0));
  }

  @Test
  public void throws500InternalServerErrorIfSomethingGoesWrong() {
    MockHttpConnection connection = new MockHttpConnection(GET, "/");
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/", GET, request -> {
      throw new RuntimeException();
    }));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    assertThrows(InternalServerError.class, () -> connectionHandler.handle(connection));

    List<String> headerOutput = connection.getHeaderWriter().lines;
    assertEquals(2, headerOutput.size());
    assertEquals("HTTP/1.1 500 Internal Server Error", headerOutput.get(0));
    assertEquals("", headerOutput.get(1));
  }

  @Test
  public void ignoreEmptyConnection() {
    MockHttpConnection emptyConnection = new EmptyHttpConnection();
    MockRequestHandler handler = new MockRequestHandler();
    Set<HttpContext> contexts = new HashSet<>();
    contexts.add(new HttpContext("/", GET, handler));

    ConnectionHandler connectionHandler = new ConnectionHandler(contexts);
    connectionHandler.handle(emptyConnection);

    assertFalse(handler.hasBeenCalled);
  }

  private static class MockRequestHandler implements RequestHandler {
    private boolean hasBeenCalled = false;
    private HttpRequest request;

    private MockRequestHandler() {

    }

    @Override
    public HttpResponse handle(HttpRequest request) {
      if (hasBeenCalled) {
        throw new IllegalStateException();
      }
      hasBeenCalled = true;
      this.request = request;
      return new HttpResponse("HTTP/1.1", 200, "OK");
    }
  }

  private static class EmptyHttpConnection extends MockHttpConnection {
    public EmptyHttpConnection() {
      super(null, null);
    }

    @Override
    public MockBufferedInputStream getInputStream() {
      return new MockBufferedInputStream(new byte[0]);
    }
  }

  private static class MockHttpConnection extends HttpConnection {
    private final HttpMethod method;
    private final String path;
    private final String httpVersion = "HTTP/1.1";
    private final MockPrintWriter mockPrintWriter = new MockPrintWriter();
    private final MockBufferedOutputStream mockBufferedOutputStream = new MockBufferedOutputStream();

    private MockBufferedInputStream mockBufferedInputStream;

    public MockHttpConnection(HttpMethod method, String path) {
      super(null);
      this.method = method;
      this.path = path;
    }

    @Override
    public MockBufferedInputStream getInputStream() {
      if (mockBufferedInputStream == null) {
        mockBufferedInputStream = createInputStream();
      }
      return mockBufferedInputStream;
    }

    @Override
    public MockPrintWriter getHeaderWriter() {
      return mockPrintWriter;
    }

    @Override
    public MockBufferedOutputStream getBodyOutputStream() {
      return mockBufferedOutputStream;
    }

    private MockBufferedInputStream createInputStream() {
      StringBuilder input = new StringBuilder();
      input.append(String.join(" ", method.name(), path, httpVersion)).append("\r\n");
      input.append("Header1: value1\r\n");
      input.append("Header2: value2\r\n");
      input.append("Header3: value3\r\n");
      input.append("Content-Length: 42\r\n");
      input.append("\r\n");
      input.append("Bodyline1\r\n");
      input.append("Bodyline2\r\n");
      input.append("Bodyline3\r\n");
      input.append("Bodyline4");
      return new MockBufferedInputStream(input.toString().getBytes());
    }
  }

  private static class MockBufferedInputStream extends BufferedInputStream {
    private boolean closed = false;

    public MockBufferedInputStream(byte[] bytes) {
      super(new ByteArrayInputStream(bytes));
    }

    @Override
    public void close() throws IOException {
      this.closed = true;
      super.close();
    }
  }

  private static class MockPrintWriter extends PrintWriter {
    private boolean closed = false;
    private final List<String> lines = new ArrayList<>();

    public MockPrintWriter() {
      super(OutputStream.nullOutputStream());
    }

    @Override
    public void println(String line) {
      this.lines.add(line);
    }

    @Override
    public void println() {
      this.lines.add("");
    }

    @Override
    public void close() {
      closed = true;
      super.close();
    }
  }

  private static class MockBufferedOutputStream extends BufferedOutputStream {
    private boolean closed = false;
    private final List<byte[]> bytes = new ArrayList<>();

    public MockBufferedOutputStream() {
      super(OutputStream.nullOutputStream());
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
      this.bytes.add(b);
    }

    @Override
    public void close() throws IOException {
      closed = true;
      super.close();
    }
  }
}
