package ch.ethy.transact.handlers;

import ch.ethy.transact.server.*;

import java.io.*;
import java.util.*;

import static ch.ethy.transact.server.ContentType.*;
import static ch.ethy.transact.server.HttpHeader.*;

public class StaticResourcesHandler implements RequestHandler {
  private final String baseWebappPath;

  public StaticResourcesHandler(String baseWebappPath) {
    this.baseWebappPath = baseWebappPath;
  }

  @Override
  public HttpResponse handle(HttpRequest request) {
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");

    try (InputStream resource = getClass().getResourceAsStream(baseWebappPath + request.getPath())) {
      byte[] body = resource.readAllBytes();
      response.setBody(body);

      String fileExtension = getFileExtension(request.getPath());
      String contentType = getContentType(fileExtension);

      response.addHeader(CONTENT_TYPE, contentType);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return response;
  }

  private String getFileExtension(String path) {
    return Optional.ofNullable(path)
        .filter(p -> p.contains("."))
        .map(p -> p.substring(p.lastIndexOf('.') + 1))
        .orElse(null);
  }

  private String getContentType(String extension) {
    return switch (extension) {
      case "css" -> TEXT_CSS;
      case "gif" -> IMAGE_GIF;
      case "html" -> TEXT_HTML;
      case "jpg", "jpeg" -> IMAGE_JPEG;
      case "js" -> APPLICATION_JAVASCRIPT;
      case "json" -> APPLICATION_JSON;
      case "png" -> IMAGE_PNG;
      default -> APPLICATION_OCTET_STREAM;
    };
  }
}
