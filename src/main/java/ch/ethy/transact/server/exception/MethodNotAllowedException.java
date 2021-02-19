package ch.ethy.transact.server.exception;

public class MethodNotAllowedException extends HttpException {
  public MethodNotAllowedException() {
    super(405, "Method Not Allowed");
  }
}
