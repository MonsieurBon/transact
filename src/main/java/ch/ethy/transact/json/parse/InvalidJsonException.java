package ch.ethy.transact.json.parse;

public class InvalidJsonException extends RuntimeException {
  private final String input;
  private final int position;

  public InvalidJsonException(String input, int position) {
    this.input = input;
    this.position = position;
  }
}
