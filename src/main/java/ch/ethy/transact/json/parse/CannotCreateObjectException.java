package ch.ethy.transact.json.parse;

public class CannotCreateObjectException extends RuntimeException {
  public CannotCreateObjectException(Exception e) {
    super(e);
  }
}
