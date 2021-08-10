package ch.ethy.transact.server.exception;

public class UnauthorizedException extends HttpException {
  public UnauthorizedException() {
    super(401, "Unauthorized");
  }
}
