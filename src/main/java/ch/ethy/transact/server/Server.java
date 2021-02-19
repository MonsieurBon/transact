package ch.ethy.transact.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
  private int port;
  private final Set<HttpContext> handlers = new HashSet<>();
  private boolean running = false;

  private Server() {}

  public static ServerBuilder onPort(int port) {
    ServerBuilder builder = new ServerBuilder();
    return builder.withPort(port);
  }

  private void setPort(int port) {
    this.port = port;
  }

  private void addHandler(HttpContext context) {
    handlers.add(context);
  }

  public void start() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    running = true;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      while(running) {
        Socket clientSock = serverSocket.accept();
        new Thread(new ServerWorker(clientSock, handlers)).start();
      }
    } catch (IOException e) {
      running = false;
    }
  }

  public void stop() {
    running = false;
  }

  public static class ServerBuilder {

    private final Server server;

    private ServerBuilder() {
      server = new Server();
    }

    public ServerBuilder withPort(int port) {
      server.setPort(port);
      return this;
    }

    public ServerBuilder addHandler(String path, HttpMethod method, RequestHandler requestHandler) {
      HttpContext context = new HttpContext(path, method, requestHandler);
      this.server.addHandler(context);
      return this;
    }
    public Server create() {
      return this.server;
    }

  }
}
