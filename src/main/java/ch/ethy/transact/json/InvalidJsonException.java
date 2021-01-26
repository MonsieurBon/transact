package ch.ethy.transact.json;

public class InvalidJsonException extends RuntimeException {
  private final String input;
  private final int position;

  public InvalidJsonException(String input, int position) {
    this.input = input;
    this.position = position;
  }
}
