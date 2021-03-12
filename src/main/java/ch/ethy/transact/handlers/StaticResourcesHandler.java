package ch.ethy.transact.handlers;

import ch.ethy.transact.server.*;
import ch.ethy.transact.server.exception.*;

import java.io.*;
import java.util.*;

import static ch.ethy.transact.server.ContentType.*;
import static ch.ethy.transact.server.HttpHeader.*;

public class StaticResourcesHandler implements RequestHandler {
  public static final String DEFAULT_FILE_NAME = "index.html";
  private final String webappBasePath;

  public StaticResourcesHandler(String webappBasePath) {
    this.webappBasePath = webappBasePath;
  }

  @Override
  public HttpResponse handle(HttpRequest request) {
    HttpResponse response = new HttpResponse("HTTP/1.1", 200, "OK");

    String path = webappBasePath + request.getPath();

    if (path.endsWith("/")) {
      path += DEFAULT_FILE_NAME;
    }

    File file = new File(path);

    try (InputStream resource = file.exists()
        ? new FileInputStream(file)
        : getClass().getResourceAsStream(path)) {
      if (resource == null) {
        throw new NotFoundException();
      }

      byte[] body = resource.readAllBytes();
      response.setBody(body);

      String fileExtension = getFileExtension(path);
      String contentType = getContentType(fileExtension);

      response.addHeader(CONTENT_TYPE, contentType);
    } catch (IOException e) {
      throw new InternalServerError(e);
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
