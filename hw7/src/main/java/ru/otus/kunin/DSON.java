package ru.otus.kunin;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.json.*;

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
      return arrayToJsonObject(o);
    }

    if (o instanceof Map) {
      return mapToJsonObject((Map<?,?>)o);
    }

    return objectToJsonObject(o);
  }

  private static JsonObject objectToJsonObject(Object o) {

    final Field[] fields = o.getClass().getFields();
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    Arrays.stream(fields)
      .filter(field -> !Modifier.isStatic(field.getModifiers()))
      .filter(field -> !Modifier.isTransient(field.getModifiers()))
      .forEach(field -> {
        final String name = fieldToName(field);
        final JsonValue value = filedValue(field, o);
        objectBuilder.add(name, value);
      });
    return objectBuilder.build();
  }

  private static JsonValue filedValue(Field field, Object o) {
    try {
      field.setAccessible(true);
      final Object value = field.get(o);
      return toJsonObject(value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static String fieldToName(Field field) {
    return Preconditions.checkNotNull(field).getName();
  }

  private static JsonObject mapToJsonObject(Map<?, ?> o) {
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    o.entrySet().forEach(entry -> {
      objectBuilder.add(String.valueOf(entry.getKey()), toJsonObject(entry.getValue()));
    });
    return objectBuilder.build();
  }

  private static JsonArray arrayToJsonObject(final Object o) {
    final Object[] asArray = o instanceof Collection<?> ? ((Collection<?>)o).toArray() : (Object[])o;
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    Stream.of(asArray).forEach(element -> jsonArrayBuilder.add(toJsonObject(element)));
    return jsonArrayBuilder.build();
  }
}
