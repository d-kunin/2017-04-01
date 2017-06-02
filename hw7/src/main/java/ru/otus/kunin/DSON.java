package ru.otus.kunin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.json.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class DSON {

  private final static int MAX_DEPTH = 32;

  @FunctionalInterface
  interface Converter extends Function<Object, JsonValue> {
  }

  private final static Map<Class<?>, Converter> PRIMITIVE_CONVERTERS = ImmutableMap.<Class<?>, Converter>builder()
      .put(Float.class,   v -> Json.createValue((double) v))
      .put(Double.class,  v -> Json.createValue((double) v))
      .put(Byte.class,    v -> Json.createValue((int) v))
      .put(Short.class,   v -> Json.createValue((int) v))
      .put(Integer.class, v -> Json.createValue((int) v))
      .put(Long.class,    v -> Json.createValue((long) v))
      .put(Boolean.class, v -> (Boolean)v ? JsonValue.TRUE : JsonValue.FALSE)
      .build();

  private final static Map<Class<?>, Converter> PRIMITIVE_ARRAYS_CONVERTERS = ImmutableMap.<Class<?>, Converter>builder()
      .put(float[].class, o -> JsonPrimitiveArrays.toJsonArray((float[]) o))
      .put(double[].class, o -> JsonPrimitiveArrays.toJsonArray((double[]) o))
      .put(byte[].class, o -> JsonPrimitiveArrays.toJsonArray((byte[]) o))
      .put(short[].class, o -> JsonPrimitiveArrays.toJsonArray((short[]) o))
      .put(int[].class, o -> JsonPrimitiveArrays.toJsonArray((int[]) o))
      .put(long[].class, o -> JsonPrimitiveArrays.toJsonArray((long[]) o))
      .put(boolean[].class, o -> JsonPrimitiveArrays.toJsonArray((boolean[]) o))
      .build();

  public static JsonValue toJsonObject(Object o) {
    return _toJsonObject(o, 0);
  }

  private static JsonValue _toJsonObject(final Object o, int depth) {
    if (depth > MAX_DEPTH) {
      //TODO(dima) custom strategies
      return JsonValue.NULL;
    }
    depth++;

    if (null == o) {
      return JsonValue.NULL;
    }

    if (o instanceof CharSequence) {
      return Json.createValue(o.toString());
    }

    if (PRIMITIVE_CONVERTERS.containsKey(o.getClass())) {
      return PRIMITIVE_CONVERTERS.get(o.getClass()).apply(o);
    }

    if (PRIMITIVE_ARRAYS_CONVERTERS.containsKey(o.getClass())) {
      return PRIMITIVE_ARRAYS_CONVERTERS.get(o.getClass()).apply(o);
    }

    if (o.getClass() == Object[].class || o instanceof Collection) {
      return arrayToJsonObject(o, depth);
    }

    if (o instanceof Map) {
      return mapToJsonObject((Map<?,?>)o, depth);
    }

    return objectToJsonObject(o, depth);
  }

  private static JsonObject objectToJsonObject(Object o, int currentDepth) {
    final Field[] fields = o.getClass().getFields();
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    Arrays.stream(fields)
      .filter(field -> !Modifier.isStatic(field.getModifiers()))
      .filter(field -> !Modifier.isTransient(field.getModifiers()))
      .forEach(field -> {
        final String name = fieldToName(field);
        final JsonValue value = filedValue(field, o, currentDepth);
        objectBuilder.add(name, value);
      });
    return objectBuilder.build();
  }

  private static JsonValue filedValue(Field field, Object o, int currentDepth) {
    try {
      field.setAccessible(true);
      final Object value = field.get(o);
      return _toJsonObject(value, currentDepth);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static String fieldToName(Field field) {
    return Preconditions.checkNotNull(field).getName();
  }

  private static JsonObject mapToJsonObject(Map<?, ?> o, int currentDepth) {
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    o.entrySet().forEach(entry -> {
      final String key = String.valueOf(entry.getKey());
      final JsonValue value = _toJsonObject(entry.getValue(), currentDepth);
      objectBuilder.add(key, value);
    });
    return objectBuilder.build();
  }

  private static JsonArray arrayToJsonObject(final Object o, final int currentDepth) {
    final Object[] asArray = o instanceof Collection<?> ? ((Collection<?>)o).toArray() : (Object[])o;
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    Stream.of(asArray).forEach(element -> jsonArrayBuilder.add(_toJsonObject(element, currentDepth)));
    return jsonArrayBuilder.build();
  }
}
