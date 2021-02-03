package ch.ethy.transact.json.serialize;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonSerializerTest {
  @Test
  public void serializeString() {
    String input = "foo";

    String json = new JsonSerializer(input).serialize();
    assertEquals("\"foo\"", json);
  }

  @Test
  public void serializeString_withDoubleQuote() {
    String input = "foo\"bar";

    String json = new JsonSerializer(input).serialize();
    assertEquals("\"foo\\\"bar\"", json);
  }

  @Test
  public void serializeString_withBackslash() {
    String input = "foo\\bar";

    String json = new JsonSerializer(input).serialize();
    assertEquals("\"foo\\\\bar\"", json);
  }

  @Test
  public void serializeNull() {
    Object input = null;

    String json = new JsonSerializer(input).serialize();
    assertEquals("null", json);
  }

  @Test
  public void serializeBoolean_true() {
    Object input = true;

    String json = new JsonSerializer(input).serialize();
    assertEquals("true", json);
  }

  @Test
  public void serializeBoolean_false() {
    Object input = false;

    String json = new JsonSerializer(input).serialize();
    assertEquals("false", json);
  }

  @Test
  public void serializeNumber_integer() {
    Object input = 42;

    String json = new JsonSerializer(input).serialize();
    assertEquals("42", json);
  }

  @Test
  public void serializeNumber_negativeInteger() {
    Object input = -42;

    String json = new JsonSerializer(input).serialize();
    assertEquals("-42", json);
  }

  @Test
  public void serializeNumber_long() {
    Object input = 4287886687L;

    String json = new JsonSerializer(input).serialize();
    assertEquals("4287886687", json);
  }

  @Test
  public void serializeNumber_negativeLong() {
    Object input = -4287886687L;

    String json = new JsonSerializer(input).serialize();
    assertEquals("-4287886687", json);
  }

  @Test
  public void serializeNumber_double() {
    Object input = 42.42;

    String json = new JsonSerializer(input).serialize();
    assertEquals("42.42", json);
  }

  @Test
  public void serializeNumber_negativeDouble() {
    Object input = -42.42;

    String json = new JsonSerializer(input).serialize();
    assertEquals("-42.42", json);
  }

  @Test
  public void serializeCollection() {
    Collection<Object> input = Arrays.asList(true, false, "foo", 42, -42.24, null);

    String json = new JsonSerializer(input).serialize();
    assertEquals("[true, false, \"foo\", 42, -42.24, null]", json);
  }

  @Test
  public void serializeCollectionWithNestedCollection() {
    List<Object> nested = new ArrayList<>();
    nested.add("bar");
    List<Object> input = new ArrayList<>();
    input.add("foo");
    input.add(nested);

    String json = new JsonSerializer(input).serialize();
    assertEquals("[\"foo\", [\"bar\"]]", json);
  }

  @Test
  public void serializeObject() {
    Object input = new TestObject();

    String json = new JsonSerializer(input).serialize();
    assertEquals("{\"stringProp\": \"myString\", \"boolProp\": false, \"nullProp\": null, \"numberProp\": 123.456, \"objectProp\": null, \"listProp\": null, \"parentProperty\": \"parentString\"}", json);
  }

  @Test
  public void serializeObjectWithNestedObject() {
    TestObject input = new TestObject();
    input.objectProp = new NestedObject();

    String json = new JsonSerializer(input).serialize();
    assertEquals("{\"stringProp\": \"myString\", \"boolProp\": false, \"nullProp\": null, \"numberProp\": 123.456, \"objectProp\": {\"nestedProperty\": \"nestedString\"}, \"listProp\": null, \"parentProperty\": \"parentString\"}", json);
  }

  @Test
  public void serializeCollectionWithNestedObject() {
    Collection<Object> input = Arrays.asList("foo", new TestObject(), "bar");

    String json = new JsonSerializer(input).serialize();
    assertEquals("[\"foo\", {\"stringProp\": \"myString\", \"boolProp\": false, \"nullProp\": null, \"numberProp\": 123.456, \"objectProp\": null, \"listProp\": null, \"parentProperty\": \"parentString\"}, \"bar\"]", json);
  }

  @Test
  public void serializeObjectWithNestedCollection() {
    TestObject input = new TestObject();
    input.listProp = List.of("foo", "bar");

    String json = new JsonSerializer(input).serialize();
    assertEquals("{\"stringProp\": \"myString\", \"boolProp\": false, \"nullProp\": null, \"numberProp\": 123.456, \"objectProp\": null, \"listProp\": [\"foo\", \"bar\"], \"parentProperty\": \"parentString\"}", json);
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class ParentObject {
    private String parentProperty = "parentString";
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class TestObject extends ParentObject {
    private String stringProp = "myString";
    private boolean boolProp = false;
    private Object nullProp = null;
    private Number numberProp = 123.456;
    private NestedObject objectProp;
    private List<Object> listProp;
  }

  @SuppressWarnings({"FieldMayBeFinal", "unused"})
  public static class NestedObject {
    private String nestedProperty = "nestedString";
  }
}
