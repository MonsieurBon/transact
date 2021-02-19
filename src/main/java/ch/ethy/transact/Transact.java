package ch.ethy.transact;

import ch.ethy.transact.server.HttpResponse;
import ch.ethy.transact.server.Server;

import java.io.IOException;

import static ch.ethy.transact.server.HttpHeader.CONTENT_TYPE;
import static ch.ethy.transact.server.HttpMethod.GET;

public class Transact {
  public static void main(String[] args) throws IOException {

//    InetSocketAddress addr = new InetSocketAddress(8080);
//    HttpServer server = HttpServer.create(addr, 0);
//    server.createContext("/", exchange -> {
//
//    });
//    server.start();

    Server server = Server.onPort(8080)
        .addHandler("/", GET, request -> {
          HttpResponse response = new HttpResponse(request.getHttpVersion(), 200, "OK");

          response.setBody("{\"foo\": 42}");
          response.addHeader(CONTENT_TYPE, "application/json");

          return response;
        })
        .create();

    server.start();
  }
}
