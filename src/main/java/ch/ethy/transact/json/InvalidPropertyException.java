package ch.ethy.transact.json;

public class InvalidPropertyException extends RuntimeException{
  public InvalidPropertyException(Exception e) {
    super(e);
  }
}
