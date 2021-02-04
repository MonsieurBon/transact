package ch.ethy.transact.json.parse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.ethy.transact.json.SpecialCharacters.BACKSLASH;
import static ch.ethy.transact.json.SpecialCharacters.CLOSING_BRACES;
import static ch.ethy.transact.json.SpecialCharacters.CLOSING_BRACKETS;
import static ch.ethy.transact.json.SpecialCharacters.COLON;
import static ch.ethy.transact.json.SpecialCharacters.COMMA;
import static ch.ethy.transact.json.SpecialCharacters.DOUBLE_QUOTES;
import static ch.ethy.transact.json.SpecialCharacters.ESCAPED_CHARS;
import static ch.ethy.transact.json.SpecialCharacters.LITERAL_TERMINATING;
import static ch.ethy.transact.json.SpecialCharacters.OPENING_BRACES;
import static ch.ethy.transact.json.SpecialCharacters.OPENING_BRACKETS;
import static ch.ethy.transact.json.SpecialCharacters.SPACE;
import static java.util.stream.Collectors.toList;

public class JsonParser {
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

      if (LITERAL_TERMINATING.contains(c)) {
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

  public <T> T parse(Class<T> clazz) {
    @SuppressWarnings("unchecked")
    Map<String, Object> propertiesMap = (Map<String, Object>) parse();

    Map<Class<?>, Map<String, Class<?>>> genericsInformation = getGenericsInformation(clazz, Collections.emptyMap());

    return mapFields(clazz, propertiesMap, genericsInformation);
  }

  @SuppressWarnings("unchecked")
  private <T> T mapFields(Class<T> clazz, Map<String, Object> propMap, Map<Class<?>, Map<String, Class<?>>> genericsInformation) {
    T newObj;
    try {
      Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
      declaredConstructor.setAccessible(true);
      newObj = declaredConstructor.newInstance();
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new CannotCreateObjectException(e);
    }

    try {
      for (Map.Entry<String, Object> property : propMap.entrySet()) {
        Field field = getField(clazz, property.getKey());
        field.setAccessible(true);
        try {
          if (property.getValue() instanceof Map) {
            Class<?> type = field.getType();

            if (!type.equals(field.getGenericType())) {
              type = genericsInformation.get(field.getDeclaringClass()).get(field.getGenericType().getTypeName());
            }

            field.set(newObj, mapFields(type, (Map<String, Object>) property.getValue(), genericsInformation));
          } else if (property.getValue() instanceof List && field.getGenericType() instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            Class<?> listType;
            if (actualTypeArgument instanceof TypeVariable) {
              listType = genericsInformation.get(field.getDeclaringClass()).get(actualTypeArgument.getTypeName());
            } else {
              listType = (Class<?>) actualTypeArgument;
            }

            List<?> mappedList = ((List<?>) property.getValue()).stream()
                .map(value -> listType.isAssignableFrom(value.getClass()) ? value : mapFields(listType, (Map<String, Object>) value, genericsInformation))
                .collect(toList());

            field.set(newObj, mappedList);

          } else {
            field.set(newObj, property.getValue());
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    } catch (NoSuchFieldException e) {
      throw new InvalidPropertyException(e);
    }

    return newObj;
  }

  private <T> Field getField(Class<T> clazz, String fieldName) throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Class<? super T> superclass = clazz.getSuperclass();
      if (superclass != null) {
        return getField(superclass, fieldName);
      }

      throw e;
    }
  }

  private Map<Class<?>, Map<String, Class<?>>> getGenericsInformation(Class<?> clazz, Map<String, Class<?>> childGenerics) {
    if (clazz.getSuperclass() == null) {
      return Collections.emptyMap();
    }

    Map<String, Class<?>> generics = new HashMap<>();

    Type genericSuperclass = clazz.getGenericSuperclass();
    Class<?> superclass = clazz.getSuperclass();
    if (genericSuperclass instanceof ParameterizedType) {
      TypeVariable<? extends Class<?>>[] typeParameters = superclass.getTypeParameters();
      Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
      for(int i = 0; i < typeParameters.length; i++) {
        Type typeArgument = actualTypeArguments[i];
        String name = typeParameters[i].getName();

        if (typeArgument instanceof TypeVariable) {
          generics.put(name, childGenerics.get(typeArgument.getTypeName()));
        } else {
          generics.put(name, (Class<?>) typeArgument);
        }
      }
    }

    Map<Class<?>, Map<String, Class<?>>> classGenerics = new HashMap<>();
    classGenerics.put(superclass, generics);
    classGenerics.putAll(getGenericsInformation(superclass, generics));

    return classGenerics;
  }
}
