package ch.ethy.transact;

import ch.ethy.transact.server.*;

import java.io.*;

import static ch.ethy.transact.server.HttpMethod.*;

public class Transact {
  private RequestHandler staticResourcesHandler;

  public static void main(String[] args) throws IOException {
    Transact app = new Transact();
    app.setup();
    app.run();
  }

  private void setup() {
//    staticResourcesHandler = new StaticResourcesHandler("/webapp");
  }

  private void run() {
    //    InetSocketAddress addr = new InetSocketAddress(8080);
//    HttpServer server = HttpServer.create(addr, 0);
//    server.createContext("/", exchange -> {
//
//    });
//    server.start();

    Server server = Server.onPort(8080)
        .addHandler("/", GET, staticResourcesHandler)
        .create();

    server.start();
  }
}
