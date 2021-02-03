package ch.ethy.transact.json;

import java.util.List;

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

  private SpecialCharacters() {};
}
