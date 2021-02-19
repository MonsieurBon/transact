package ch.ethy.transact.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpConnection implements Closeable {
  private final Socket clientSocket;
  private InputStream in;
  private OutputStream out;

  public HttpConnection(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public BufferedInputStream getInputStream() throws IOException {
    if (in == null) {
      in = clientSocket.getInputStream();
    }
    return new BufferedInputStream(in);
  }

  public PrintWriter getHeaderWriter() throws IOException {
    if (out == null) {
      out = clientSocket.getOutputStream();
    }
    return new PrintWriter(out);
  }

  public BufferedOutputStream getBodyOutputStream() throws IOException {
    if (out == null) {
      out = clientSocket.getOutputStream();
    }
    return new BufferedOutputStream(out);
  }

  @Override
  public void close() throws IOException {
    if (in != null) {
      in.close();
    }

    if (out != null) {
      out.close();
    }

    this.clientSocket.close();
  }
}
