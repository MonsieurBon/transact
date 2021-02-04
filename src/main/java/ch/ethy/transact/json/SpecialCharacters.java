package ch.ethy.transact.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SpecialCharacters {
  public static final char SPACE = 32;
  public static final char DOUBLE_QUOTES = 34;
  public static final char COMMA = 44;
  public static final char COLON = 58;
  public static final char OPENING_BRACKETS = 91;
  public static final char BACKSLASH = 92;
  public static final char CLOSING_BRACKETS = 93;
  public static final char OPENING_BRACES = 123;
  public static final char CLOSING_BRACES = 125;

  public static final List<Character> ESCAPED_CHARS = List.of(BACKSLASH, DOUBLE_QUOTES);
  public static final List<Character> LITERAL_TERMINATING = new ArrayList<>();

  static {
    IntStream.rangeClosed(0, SPACE)
        .forEach(value -> LITERAL_TERMINATING.add((char) value));
    LITERAL_TERMINATING.addAll(List.of(COMMA, CLOSING_BRACES, CLOSING_BRACKETS));
  }

  private SpecialCharacters() {};
}
