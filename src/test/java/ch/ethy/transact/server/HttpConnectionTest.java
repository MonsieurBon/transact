package ch.ethy.transact.server;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpConnectionTest {
  @Test
  public void returnsInputReader() throws IOException {
    MockSocket socket = new MockSocket();
    HttpConnection connection = new HttpConnection(socket);
    MockInputStream mockInputStream = socket.getInputStream();
    assertFalse(mockInputStream.closed);

    BufferedInputStream in = connection.getInputStream();
    in.close();

    assertTrue(mockInputStream.closed);
  }

  @Test
  public void returnsOutputPrinter() throws IOException {
    MockSocket socket = new MockSocket();
    HttpConnection connection = new HttpConnection(socket);
    MockOutputStream mockOutputStream = socket.getOutputStream();
    assertFalse(mockOutputStream.closed);

    PrintWriter writer = connection.getHeaderWriter();
    writer.close();

    assertTrue(mockOutputStream.closed);
  }

  private static class MockSocket extends Socket {
    MockInputStream mockInputStream = new MockInputStream();
    MockOutputStream mockOutputStream = new MockOutputStream();

    @Override
    public MockInputStream getInputStream() {
      return mockInputStream;
    }

    @Override
    public MockOutputStream getOutputStream() {
      return mockOutputStream;
    }
  }

  private static class MockInputStream extends InputStream {
    private boolean closed = false;
    @Override
    public void close() {
      closed = true;
    }

    @Override
    public int read() {
      return 0;
    }
  }

  private static class MockOutputStream extends OutputStream {
    private boolean closed = false;


    @Override
    public void write(int b) {

    }

    @Override
    public void close() {
      closed = true;
    }
  }
}
