package ch.ethy.transact.server.exception;

public class InternalServerError extends HttpException {
  public InternalServerError() {
    super(500, "Internal Server Error");
  }

  public InternalServerError(Exception e) {
    this();
    this.initCause(e);
  }
}
