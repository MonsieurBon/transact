package ch.ethy.transact.server.exception;

public class NotFoundException extends HttpException {
  public NotFoundException() {
    super(404, "Not Found");
  }
}
