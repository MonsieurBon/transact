package ch.ethy.transact.json.parse;

public class InvalidPropertyException extends RuntimeException{
  public InvalidPropertyException(Exception e) {
    super(e);
  }
}
