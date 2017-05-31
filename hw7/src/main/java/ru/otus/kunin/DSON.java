package ru.otus.kunin;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;

public class DSON {

  @FunctionalInterface
  interface Converter extends Function<Object, JsonValue> {
  }

  private final static Map<Class<?>, Converter> PRIMITIVE_CONVERTERS = ImmutableMap.<Class<?>, Converter>builder()
      .put(Double.class,  v -> Json.createValue((double) v))
      .put(Float.class,   v -> Json.createValue((double) v))
      .put(Byte.class,    v -> Json.createValue((int) v))
      .put(Short.class,   v -> Json.createValue((int) v))
      .put(Integer.class, v -> Json.createValue((int) v))
      .put(Long.class,    v -> Json.createValue((long) v))
      .put(Boolean.class, v -> (Boolean)v ? JsonValue.TRUE : JsonValue.FALSE)
      .build();

  private final static Map<Class<?>, Converter> PRIMITIVE_ARRAYS_CONVERTERS = ImmutableMap.<Class<?>, Converter>builder()
      .put(byte[].class, o -> JsonPrimitiveArrays.toJsonArray((byte[]) o))
      .put(short[].class, o -> JsonPrimitiveArrays.toJsonArray((short[]) o))
      .put(int[].class, o -> JsonPrimitiveArrays.toJsonArray((int[]) o))
      .put(long[].class, o -> JsonPrimitiveArrays.toJsonArray((long[]) o))
      .put(boolean[].class, o -> JsonPrimitiveArrays.toJsonArray((boolean[]) o))
      .put(float[].class, o -> JsonPrimitiveArrays.toJsonArray((float[]) o))
      .put(double[].class, o -> JsonPrimitiveArrays.toJsonArray((double[]) o))
      .build();

  public static JsonValue toJsonObject(Object o) {
    if (null == o) {
      return JsonValue.NULL;
    }

    if (PRIMITIVE_CONVERTERS.containsKey(o.getClass())) {
      return PRIMITIVE_CONVERTERS.get(o.getClass()).apply(o);
    }

    if (PRIMITIVE_ARRAYS_CONVERTERS.containsKey(o.getClass())) {
      return PRIMITIVE_ARRAYS_CONVERTERS.get(o.getClass()).apply(o);
    }

    if (o.getClass() == Object[].class || o instanceof Collection) {
      return arrayToJsonObject(o);
    }

    if (o instanceof CharSequence) {
      return Json.createValue(o.toString());
    }

    throw new UnsupportedOperationException("Type not supported: " + o.getClass());
  }

  private static JsonArray arrayToJsonObject(final Object o) {
    final Object[] asArray = o instanceof Collection<?> ? ((Collection<?>)o).toArray() : (Object[])o;
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    Stream.of(asArray).forEach(element -> jsonArrayBuilder.add(toJsonObject(element)));
    return jsonArrayBuilder.build();
  }
}
