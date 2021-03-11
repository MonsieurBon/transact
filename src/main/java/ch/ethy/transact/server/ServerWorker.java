package ch.ethy.transact.server;

import ch.ethy.transact.log.Logger;
import ch.ethy.transact.server.exception.*;

import java.net.Socket;
import java.util.Set;

public class ServerWorker implements Runnable {
  private static final Logger LOG = Logger.getLogger(ServerWorker.class);

  private final HttpConnection httpConnection;
  private final ConnectionHandler connectionHandler;

  public ServerWorker(Socket clientSocket, Set<HttpContext> httpContexts) {
    this.httpConnection = new HttpConnection(clientSocket);
    this.connectionHandler = new ConnectionHandler(httpContexts);
  }

  @Override
  public void run() {
    try (httpConnection) {
      this.connectionHandler.handle(httpConnection);
    } catch (InternalServerError e) {
      String message = "An exception occurred while handling incoming HTTP connection";
      LOG.error(message, e);
    } catch (Exception e) {
      String message = "A non 200 response will be sent to the client";
      LOG.debug(message, e);
    }
  }
}
