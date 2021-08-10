package ch.ethy.transact;

import ch.ethy.transact.authentication.*;
import ch.ethy.transact.handlers.*;
import ch.ethy.transact.handlers.AuthenticationHandler.*;
import ch.ethy.transact.log.*;
import ch.ethy.transact.security.*;
import ch.ethy.transact.server.*;

import java.time.*;
import java.util.*;
import java.util.function.*;

import static ch.ethy.transact.handlers.middleware.JsonMiddleware.*;
import static ch.ethy.transact.security.SecurityHelper.*;
import static ch.ethy.transact.server.HttpMethod.*;

public class Transact {
  private static final Logger LOG = Logger.getLogger(Transact.class);

  private static final String WEBAPP_BASE_PATH_OPTION = "--webappBasePath";
  private static final String WEBAPP_BASE_PATH_DEFAULT = "/webapp";

  private StaticResourcesHandler staticResourcesHandler;
  private AuthenticationHandler authenticationHandler;

  public static void main(String[] args) {
    Transact app = new Transact();

    String webappBasePath = getArgumentValue(args, WEBAPP_BASE_PATH_OPTION, WEBAPP_BASE_PATH_DEFAULT);

    try {
      app.setup(webappBasePath);
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

  private void setup(String webappBasePath) {
    Logger.addAppender(new ConsoleAppender());

    final byte[] serverKey = generateRandomKey();
    Function<String, String> passwordHasher = passwordHasher(120000, 128, 64);
    BiPredicate<String, String> passwordVerifier = passwordVerifier();

    UserProvider userProvider = username -> null;
    AuthenticationService authenticationService = new AuthenticationService(ZonedDateTime::now, UUID::randomUUID, userProvider, passwordVerifier, serverKey);

    authenticationHandler = new AuthenticationHandler(authenticationService);
    staticResourcesHandler = new StaticResourcesHandler(webappBasePath);
  }

  private void run() {
    Server server = Server.onPort(8080)
        .addHandler("/", GET, staticResourcesHandler)
        .addHandler("/login", POST, asJson(fromJson((req, res, credentials) -> authenticationHandler.login(credentials), Credentials.class)))
        .create();

    server.start();
  }

  private byte[] generateRandomKey() {
    return SecurityHelper.randomBytes(256).get();
  }
}
