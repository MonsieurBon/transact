package ch.ethy.transact.server.exception;

public class HttpException extends RuntimeException {
  private final int code;
  private final String message;

  HttpException(int code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
