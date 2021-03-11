package ch.ethy.transact.server;

import ch.ethy.transact.server.exception.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static ch.ethy.transact.server.HttpHeader.*;

public class ConnectionHandler {
  private final Set<HttpContext> httpContexts;

  public ConnectionHandler(Set<HttpContext> httpContexts) {
    this.httpContexts = httpContexts;
  }

  public void handle(HttpConnection httpConnection) {
    HttpRequest request = null;
    try {
      request = readHttpRequest(httpConnection);
      if (request == null) {
        return;
      }

      String path = request.getPath();
      HttpMethod method = request.getMethod();

      List<HttpContext> handlersForPath = httpContexts.stream()
          .filter(context -> path.startsWith(context.getPath()))
          .sorted((o1, o2) -> {
            String path1 = o1.getPath();
            String path2 = o2.getPath();
            if (path1.equals(path2)) {
              return 0;
            }

            int length1 = path1.length();
            int length2 = path2.length();

            return length2 - length1;
          })
          .collect(Collectors.toList());

      if (handlersForPath.size() == 0) {
        throw new NotFoundException();
      }

      RequestHandler requestHandler = handlersForPath.stream()
          .filter(context -> context.getMethod().equals(method))
          .findFirst()
          .map(HttpContext::getHandler)
          .orElseThrow(() -> {
            throw new MethodNotAllowedException();
          });

      HttpResponse response = requestHandler.handle(request);
      writeHttpResponse(httpConnection, response);
    } catch (Exception e) {
      HttpException httpException = toHttpException(e);
      try {
        writeHttpResponse(httpConnection, createErrorResponse(request, httpException));
      } catch (IOException ioException) {
        throw httpException;
      }
      throw httpException;
    }
  }

  private HttpResponse createErrorResponse(HttpRequest request, HttpException httpException) {
    String httpVersion = request != null ? request.getHttpVersion() : "HTTP/1.1";
    return new HttpResponse(httpVersion, httpException.getCode(), httpException.getMessage());
  }

  private HttpException toHttpException(Exception e) {
    if (e instanceof HttpException) {
      return (HttpException) e;
    }

    return new InternalServerError(e);
  }

  private HttpRequest readHttpRequest(HttpConnection httpConnection) throws IOException {
    BufferedInputStream in = httpConnection.getInputStream();

    String requestLine = readLine(in);
    if (requestLine.length() == 0) {
      return null;
    }

    StringTokenizer requestLineParser = new StringTokenizer(requestLine);
    HttpMethod method = HttpMethod.get(requestLineParser.nextToken().toUpperCase());
    String path = requestLineParser.nextToken();
    String httpVersion = requestLineParser.nextToken();

    Map<String, String> headers = new HashMap<>();
    for (String header = readLine(in); !header.equals(""); header = readLine(in)) {
      StringTokenizer headerParser = new StringTokenizer(header, ": ");
      headers.put(headerParser.nextToken(), headerParser.nextToken());
    }

    byte[] body = {};
    if (headers.containsKey(CONTENT_LENGTH)) {
      int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
      body = in.readNBytes(contentLength);
    }

    return new HttpRequest(method, path, httpVersion, headers, body);
  }

  private String readLine(BufferedInputStream in) throws IOException {
    StringBuilder sb = new StringBuilder();
    int c;
    while ((c = in.read()) >= 0) {
      if (c == '\n') break;
      if (c == '\r') {
        c = in.read();
        if ((c < 0) || (c == '\n')) break;
        sb.append('\r');
      }
      sb.append((char) c);
    }
    return sb.toString();
  }

  private void writeHttpResponse(HttpConnection httpConnection, HttpResponse response) throws IOException {
    writeHeaders(httpConnection, response);
    writeBody(httpConnection, response);
  }

  private void writeBody(HttpConnection httpConnection, HttpResponse response) throws IOException {
    byte[] body = response.getBody();

    if (body.length == 0) {
      return;
    }

    BufferedOutputStream bodyOut = httpConnection.getBodyOutputStream();
    bodyOut.write(body, 0, body.length);
    bodyOut.flush();
  }

  private void writeHeaders(HttpConnection httpConnection, HttpResponse response) throws IOException {
    PrintWriter headerWriter = httpConnection.getHeaderWriter();
    String responseLine = String.join(" ", response.getHttpVersion(), Integer.toString(response.getCode()), response.getMessage());
    headerWriter.println(responseLine);

    response.getHeaders().entrySet().stream()
        .map(headerEntry -> String.join(": ", headerEntry.getKey(), headerEntry.getValue()))
        .forEach(headerWriter::println);

    headerWriter.println();
    headerWriter.flush();
  }
}
