package ch.ethy.transact;

import ch.ethy.transact.handlers.*;
import ch.ethy.transact.log.*;
import ch.ethy.transact.server.*;

import java.util.*;

import static ch.ethy.transact.server.HttpMethod.*;

public class Transact {
  public static final String WEBAPP_BASE_PATH_OPTION = "--webappBasePath";
  public static final String WEBAPP_BASE_PATH_DEFAULT = "/webapp";
  private RequestHandler staticResourcesHandler;

  public static void main(String[] args) {
    Transact app = new Transact();

    String webappBasePath = Arrays.stream(args)
        .dropWhile(arg -> !WEBAPP_BASE_PATH_OPTION.equals(arg))
        .limit(2)
        .skip(1)
        .findFirst()
        .orElse(WEBAPP_BASE_PATH_DEFAULT);

    app.setup(webappBasePath);
    app.run();
  }

  private void setup(String webappBasePath) {
    Logger.addAppender(new ConsoleAppender());

    staticResourcesHandler = new StaticResourcesHandler(webappBasePath);
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
