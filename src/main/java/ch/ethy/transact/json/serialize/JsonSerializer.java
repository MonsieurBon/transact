package ch.ethy.transact.json.serialize;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ch.ethy.transact.json.SpecialCharacters.ESCAPED_CHARS;
import static java.util.stream.Collectors.joining;

public class JsonSerializer {
  private final Object input;

  public JsonSerializer(Object input) {
    this.input = input;
  }

  public String serialize() {
    return doSerialize(input);
  }

  private String doSerialize(Object input) {
    if (input == null) {
      return serializeNull();
    }
    if (input instanceof Boolean) {
      return serializeBoolean((boolean) input);
    }
    if (input instanceof Number) {
      return serializeNumber((Number) input);
    }
    if (input instanceof String) {
      return serializeString((String) input);
    }
    if (input instanceof Collection) {
      return serializeCollection((Collection<Object>) input);
    }
    return serializeObject(input);
  }

  private String serializeObject(Object input) {
    StringBuilder jsonBuilder = new StringBuilder();

    jsonBuilder.append("{");

    String properties = getClassHierarchy(input)
        .map(Class::getDeclaredFields)
        .flatMap(Arrays::stream)
        .peek(field -> field.setAccessible(true))
        .map(field -> {
          try {
            String propertyName = field.getName();
            String propertyValue = doSerialize(field.get(input));
            return String.format("\"%s\": %s", propertyName, propertyValue);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }).collect(joining(", "));

    jsonBuilder.append(properties);

    jsonBuilder.append("}");

    return jsonBuilder.toString();
  }

  private Stream<Class<?>> getClassHierarchy(Object input) {
    Spliterator<Class<?>> classSpliterator = Spliterators.spliteratorUnknownSize(new ClassHierarchyIterator(input.getClass()), Spliterator.NONNULL);
    return StreamSupport.stream(classSpliterator, false);
  }

  private String serializeCollection(Collection<Object> input) {
    StringBuilder jsonBuilder = new StringBuilder();

    jsonBuilder.append("[");

    String objects = input.stream()
        .map(this::doSerialize)
        .collect(joining(", "));

    jsonBuilder.append(objects);

    jsonBuilder.append("]");

    return jsonBuilder.toString();
  }

  private String serializeNumber(Number input) {
    return input.toString();
  }

  private String serializeBoolean(boolean input) {
    return Boolean.toString(input);
  }

  private String serializeNull() {
    return "null";
  }

  private String serializeString(String input) {
    StringBuilder jsonBuilder = new StringBuilder();

    jsonBuilder.append('"');

    for(int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);

      if (ESCAPED_CHARS.contains(c)) {
        jsonBuilder.append('\\');
      }

      jsonBuilder.append(c);
    }

    jsonBuilder.append('"');

    return jsonBuilder.toString();
  }

  private static class ClassHierarchyIterator implements Iterator<Class<?>> {

    private Class<?> next;

    public ClassHierarchyIterator(Class<?> clazz) {
      this.next = clazz;
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public Class<?> next() {
      Class<?> tmp = next;
      next = next.getSuperclass();
      return tmp;
    }
  }
}
