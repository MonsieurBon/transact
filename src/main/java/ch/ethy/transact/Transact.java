package ch.ethy.transact;

import ch.ethy.transact.handlers.*;
import ch.ethy.transact.log.*;
import ch.ethy.transact.server.*;

import java.util.*;

import static ch.ethy.transact.server.HttpMethod.*;

public class Transact {
  private static final Logger LOG = Logger.getLogger(Transact.class);

  private static final String WEBAPP_BASE_PATH_OPTION = "--webappBasePath";
  private static final String WEBAPP_BASE_PATH_DEFAULT = "/webapp";
  private static final String SERVER_SECRET_OPTION = "--serverSecret";
  private static final String SERVER_SECRET_DEFAULT = "secret";

  private RequestHandler staticResourcesHandler;

  public static void main(String[] args) {
    Transact app = new Transact();

    String webappBasePath = getArgumentValue(args, WEBAPP_BASE_PATH_OPTION, WEBAPP_BASE_PATH_DEFAULT);
    String serverSecret = getArgumentValue(args, SERVER_SECRET_OPTION, SERVER_SECRET_DEFAULT);

    try {
      app.setup(webappBasePath, serverSecret);
    } catch (Exception e) {
      LOG.fatal("Failed to setup application", e);
      System.exit(1);
    }
    app.run();
  }

  private static String getArgumentValue(String[] args, String webappBasePathOption, String webappBasePathDefault) {
    return Arrays.stream(args)
        .dropWhile(arg -> !webappBasePathOption.equals(arg))
        .limit(2)
        .skip(1)
        .findFirst()
        .orElse(webappBasePathDefault);
  }

  private void setup(String webappBasePath, String serverSecret) {
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
