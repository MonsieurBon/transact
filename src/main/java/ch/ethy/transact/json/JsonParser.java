package ch.ethy.transact.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonParser {
  private static final char SPACE = 32;
  private static final char DOUBLE_QUOTES = 34;
  private static final char COMMA = 44;
  private static final char COLON = 58;
  private static final char OPENING_BRACKETS = 91;
  private static final char BACKSLASH = 92;
  private static final char CLOSING_BRACKETS = 93;
  private static final char OPENING_BRACES = 123;
  private static final char CLOSING_BRACES = 125;

  private static final List<Character> ESCAPED_CHARS = List.of(BACKSLASH, DOUBLE_QUOTES);

  private static final Pattern intPattern = Pattern.compile("^[0-9]+$");

  private final String input;
  private int position = 0;

  public JsonParser(String input) {
    this.input = input.trim();
  }

  public Object parse() {
    Object result = parseValue();

    if (position < input.length()) {
      throw new InvalidJsonException(input, position);
    }

    return result;
  }

  private Object parseValue() {
    char c = input.charAt(position);

    Object result = switch (c) {
      case OPENING_BRACES -> parseObject();
      case OPENING_BRACKETS -> parseCollection();
      case DOUBLE_QUOTES -> parseString();
      default -> parseLiteral();
    };
    return result;
  }

  private Collection<Object> parseCollection() {
    Collection<Object> collection = new ArrayList<>();
    position++;

    for(; position < input.length() && input.charAt(position) != CLOSING_BRACKETS; position++) {
      eatWhiteSpace();
      collection.add(parseValue());
      eatWhiteSpace();

      if (COMMA != input.charAt(position)) {
        break;
      }
    }

    if (position < input.length() && CLOSING_BRACKETS == input.charAt(position)) {
      position++;
      return collection;
    }

    throw new InvalidJsonException(input, position);
  }

  private Map<String, Object> parseObject() {
    Map<String, Object> objMap = new HashMap<>();
    position++;

    for(; position < input.length() && input.charAt(position) != CLOSING_BRACES; position++) {
      eatWhiteSpace();
      String propertyName = parsePropertyName();
      eatWhiteSpace();

      if (COLON == input.charAt(position)) {
        position++;
      }

      eatWhiteSpace();
      Object propertyValue = parseValue();
      eatWhiteSpace();

      objMap.put(propertyName, propertyValue);

      if (COMMA != input.charAt(position)) {
        break;
      }
    }

    if (position < input.length() && CLOSING_BRACES == input.charAt(position)) {
      position++;
      return objMap;
    }

    throw new InvalidJsonException(input, position);
  }

  private void eatWhiteSpace() {
    for (; position < input.length(); position++) {
      if (SPACE < input.charAt(position)) {
        return;
      }
    }
  }

  private String parsePropertyName() {
    if (DOUBLE_QUOTES == input.charAt(position)) {
      return parseString();
    }

    throw new InvalidJsonException(input, position);
  }

  private String parseString() {
    StringBuilder stringBuilder = new StringBuilder();
    position++;

    for (; position < input.length(); position++) {
      char c = input.charAt(position);

      if (c == DOUBLE_QUOTES) {
        position++;
        return stringBuilder.toString();
      }

      if (c == BACKSLASH) {
        if (input.length() > position + 1 && ESCAPED_CHARS.contains(input.charAt(position + 1))) {
          c = input.charAt(++position);
        } else {
          throw new InvalidJsonException(input, position);
        }
      }

      stringBuilder.append(c);
    }

    throw new InvalidJsonException(input, position);
  }

  private Object parseLiteral() {
    StringBuilder literalBuilder = new StringBuilder();
    int startingPosition = position;

    for (; position < input.length(); position++) {
      char c = input.charAt(position);

      if (c <= SPACE || c == COMMA) {
        break;
      }

      literalBuilder.append(c);
    }

    String literal = literalBuilder.toString();

    return switch (literal) {
      case "null" -> null;
      case "true" -> true;
      case "false" -> false;
      default -> {
        try {
          yield toNumber(literal);
        } catch (NumberFormatException e) {
          throw new InvalidJsonException(input, startingPosition);
        }
      }
    };
  }

  private Number toNumber(String literal) {
    if (literal.contains(".")) {
      return toFloatingPointNumber(literal);
    } else {
      return toWholeNumber(literal);
    }
  }

  private Number toWholeNumber(String literal) {
    long l = Long.parseLong(literal);

    if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
      return l;
    } else {
      return (int) l;
    }
  }

  private Double toFloatingPointNumber(String literal) {
    return Double.parseDouble(literal);
  }
}
