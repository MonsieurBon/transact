package ch.ethy.transact.json.parse;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
class JsonParserTest {
  @Test
  public void parseNull() {
    String input = "null";

    Object obj = new JsonParser(input).parse();
    assertNull(obj);
  }

  @Test
  public void parseBoolean_true() {
    String input = "true";

    boolean bool = (boolean) new JsonParser(input).parse();
    assertTrue(bool);
  }

  @Test
  public void parseBoolean_false() {
    String input = "false";

    boolean bool = (boolean) new JsonParser(input).parse();
    assertFalse(bool);
  }

  @Test
  public void parseInteger() {
    String input = "42";

    int integer = (int) new JsonParser(input).parse();
    assertEquals(42, integer);
  }

  @Test
  public void parseNegativeInteger() {
    String input = "-12345";

    int integer = (int) new JsonParser(input).parse();
    assertEquals(-12345, integer);
  }

  @Test
  public void parseLong() {
    String input = "4294967294";

    long longer = (long) new JsonParser(input).parse();
    assertEquals(4294967294L, longer);
  }

  @Test
  public void parseNegativeLong() {
    String input = "-112233445566778899";

    long longer = (long) new JsonParser(input).parse();
    assertEquals(-112233445566778899L, longer);
  }

  @Test
  public void parseDouble() {
    String input = "42.42";

    double d = (double) new JsonParser(input).parse();
    assertEquals(42.42, d);
  }

  @Test
  public void parseNegativeDouble() {
    String input = "-42.42";

    double d = (double) new JsonParser(input).parse();
    assertEquals(-42.42, d);
  }

  @Test
  public void parseInvalidLiteral_throwsException() {
    String input = "foobar";

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseString() {
    String input = "\"foo\"";

    String str = (String) new JsonParser(input).parse();
    assertEquals("foo", str);
  }

  @Test
  public void parseString_noClosingQuotes() {
    String input = "\"foo";

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseString_escapedDoubleQoutes() {
    String input = """
        "foo\\"bar"
        """;

    String str = (String) new JsonParser(input).parse();
    assertEquals("foo\"bar", str);
  }

  @Test
  public void parseString_escapedBackslash() {
    String input = """
        "\\\\"
        """;

    String str = (String) new JsonParser(input).parse();
    assertEquals("\\", str);
  }

  @Test
  public void parseString_invalidEscape() {
    String input = """
        "\\a"
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseString_escapeAtEndOfString() {
    String input = """
        "\\
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseString_charactersAfterClosingQuotes() {
    String input = """
        "foo"b
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_empty() {
    String input = "{}";

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Collections.emptyMap(), objMap);
  }

  @Test
  public void parseObject_notClosed() {
    String input = "{";

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_stringProperty() {
    String input = """
        {
          "foo": "bar"
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", "bar"), objMap);
  }

  @Test
  public void parseObject_booleanPropertyTrue() {
    String input = """
        {
          "foo": true
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", true), objMap);
  }

  @Test
  public void parseObject_booleanPropertyFalse() {
    String input = """
        {
          "foo": false
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", false), objMap);
  }

  @Test
  public void parseObject_nullProperty() {
    String input = """
        {
          "foo": null
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(1, objMap.size());
    assertTrue(objMap.containsKey("foo"));
    assertNull(objMap.get("foo"));
  }

  @Test
  public void parseObject_numericalProperty() {
    String input = """
        {
          "foo": -42.42
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", -42.42), objMap);
  }

  @Test
  public void parseObject_multipleProperties() {
    String input = """
        {
          "foo": "bar",
          "baz": true
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", "bar", "baz", true), objMap);
  }

  @Test
  public void parseObject_multipleProperties_missingComma() {
    String input = """
        {
          "foo": "bar"
          "baz": true
        }
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_trailingComma() {
    String input = """
        {
          "foo": "bar",
        }
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_missingValue() {
    String input = """
        {
          "foo": ,
          "bar": "baz"
        }
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_missingProperty() {
    String input = """
        {
          : "foo",
        }
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseObject_nestedObject() {
    String input = """
        {
          "foo": "bar",
          "baz": {
            "bla": true
          }
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", "bar", "baz", Map.of("bla", true)), objMap);
  }

  @Test
  public void parseCollection_empty() {
    String input = "[]";

    List<Object> collection = (List<Object>) new JsonParser(input).parse();
    assertEquals(Collections.emptyList(), collection);
  }

  @Test
  public void parseCollection_notClosed() {
    String input = "[";

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseCollection_stringValue() {
    String input = """
        [
          "foo"
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of("foo"), collection);
  }

  @Test
  public void parseCollection_booleanValueTrue() {
    String input = """
        [
          true
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of(true), collection);
  }

  @Test
  public void parseCollection_booleanValueFalse() {
    String input = """
        [
          false
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of(false), collection);
  }

  @Test
  public void parseCollection_nullValue() {
    String input = """
        [
          null
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(1, collection.size());
    assertTrue(collection.contains(null));
  }

  @Test
  public void parseCollection_numericalValue() {
    String input = """
        [
          -24.42
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of(-24.42), collection);
  }

  @Test
  public void parseCollection_multipleProperties() {
    String input = """
        [
          "foo",
          true
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of("foo", true), collection);
  }

  @Test
  public void parseCollection_multipleValues_missingComma() {
    String input = """
        [
          "foo"
          true
        ]
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseCollection_trailingComma() {
    String input = """
        [
          "foo",
        ]
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidJsonException.class, parser::parse);
  }

  @Test
  public void parseCollection_nestedCollection() {
    String input = """
        [
          "foo",
          [
            true
          ]
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of("foo", List.of(true)), collection);
  }

  @Test
  public void parseObject_nestedCollection() {
    String input = """
        {
          "foo": "bar",
          "baz": [ true ]
        }
        """;

    Map<String, Object> objMap = (Map<String, Object>) new JsonParser(input).parse();
    assertEquals(Map.of("foo", "bar", "baz", List.of(true)), objMap);
  }

  @Test
  public void parseCollection_nestedObject() {
    String input = """
        [
          "foo",
          {
            "bar": true,
            "baz": 12345
          }
        ]
        """;

    Collection<Object> collection = (Collection<Object>) new JsonParser(input).parse();
    assertEquals(List.of("foo", Map.of("bar", true, "baz", 12345)), collection);
  }

  @Test
  public void parseToObject() {
    String input = """
        {
          "stringProp": "foobar",
          "boolProp": true,
          "nullProp": null,
          "numberProp": 246531
        }
        """;

    TestObject testObj = new JsonParser(input).parse(TestObject.class);
    assertEquals("foobar", testObj.stringProp);
    assertTrue(testObj.boolProp);
    assertNull(testObj.nullProp);
  }

  @Test
  public void parseToObject_nestedObject() {
    String input = """
        {
          "stringProp": "foobar",
          "boolProp": true,
          "nullProp": null,
          "numberProp": 246531,
          "objectProp": {
            "nestedProp": "bazzinga"
          }
        }
        """;

    TestObject testObj = new JsonParser(input).parse(TestObject.class);
    assertNotNull(testObj.objectProp);
    assertEquals("bazzinga", testObj.objectProp.nestedProp);
  }

  @Test
  public void parseToObject_nestedCollection() {
    String input = """
        {
          "listProp": ["baz"]
        }
        """;

    TestObject testObj = new JsonParser(input).parse(TestObject.class);
    assertNotNull(testObj.listProp);
    assertEquals(1, testObj.listProp.size());
    assertEquals("baz", testObj.listProp.get(0));
  }

  @Test
  public void parseToObject_parentProperty() {
    String input = """
        {
          "parentProp": "foobar"
        }
        """;

    TestObject testObj = new JsonParser(input).parse(TestObject.class);
    assertEquals("foobar", testObj.getParentProp());
  }

  @Test
  public void parseToObject_noDefaultConstructor() {
    String input = "{}";

    JsonParser parser = new JsonParser(input);
    assertThrows(CannotCreateObjectException.class, () -> parser.parse(NoDefaultConstructor.class));
  }

  @Test
  public void parseToObject_invalidProperty() {
    String input = """
        {
          "invalidProperty": "value"
        }
        """;

    JsonParser parser = new JsonParser(input);
    assertThrows(InvalidPropertyException.class, () -> parser.parse(TestObject.class));
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class ParentClass {
    private String parentProp = "parentString";

    String getParentProp() {
      return parentProp;
    }
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class TestObject extends ParentClass {
    private String stringProp = "myString";
    private boolean boolProp = false;
    private Object nullProp = new Object();
    private Number numberProp = 0;
    private NestedObject objectProp;
    private List<Object> listProp;
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class NestedObject {
    private String nestedProp = "nestedString";
  }

  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  public static class NoDefaultConstructor {
    private final String foo;

    public NoDefaultConstructor(String foo) {
      this.foo = foo;
    }
  }
}