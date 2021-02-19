package ch.ethy.transact.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class WorkerRunnable implements Runnable{
  private final Socket clientSocket;
  private final Set<HttpContext> handlers;

  public WorkerRunnable(Socket clientSocket, Set<HttpContext> handlers) {
    this.clientSocket = clientSocket;
    this.handlers = handlers;
  }

  @Override
  public void run() {
    try (clientSocket;
         InputStream inputStream = clientSocket.getInputStream();
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
         PrintWriter headerOut = new PrintWriter(clientSocket.getOutputStream())) {

      String input = bufferedReader.readLine();

      if (input == null) {
        return;
      }

      StringTokenizer parse = new StringTokenizer(input);
      HttpMethod method = HttpMethod.get(parse.nextToken().toUpperCase());
      String path = parse.nextToken();
      String httpVersion = parse.nextToken();

      List<String> headers = new ArrayList<>();
      for(String header = bufferedReader.readLine(); header != null && header.length() > 0; header = bufferedReader.readLine()) {
        headers.add(header);
      }

//      Handler handler = handlers.get(null);

//      HttpResponse response = handler.handle(request);

      String httpResponse = "HTTP/1.1 200 OK";
      headerOut.println(httpResponse);
      headerOut.println();
      headerOut.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
