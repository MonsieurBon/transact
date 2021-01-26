package ch.ethy.transact.json;

public class CannotCreateObjectException extends RuntimeException {
  public CannotCreateObjectException(Exception e) {
    super(e);
  }
}
