package ru.otus.kunin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.json.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DSON {

  private final static int MAX_DEPTH = 32;

  private final static Map<Class<?>, ConvertToJsonValue> PRIMITIVE_CONVERTERS = ImmutableMap.<Class<?>, ConvertToJsonValue>builder()
      .put(Float.class,   v -> Json.createValue(((Float) v).doubleValue()))
      .put(Double.class,  v -> Json.createValue((double) v))
      .put(Byte.class,    v -> Json.createValue(((Byte)v).intValue()))
      .put(Short.class,   v -> Json.createValue(((Short)v).intValue()))
      .put(Integer.class, v -> Json.createValue((int) v))
      .put(Long.class,    v -> Json.createValue((long) v))
      .put(Boolean.class, v -> (Boolean)v ? JsonValue.TRUE : JsonValue.FALSE)
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

    if (o.getClass().isArray()) {
      return arrayToJsonArray(o, depth);
    }

    if (o instanceof Collection) {
      return collectionToJsonArray((Collection<Object>) o, depth);
    }

    if (o instanceof Map) {
      return mapToJsonObject((Map<?,?>)o, depth);
    }

    return objectToJsonObject(o, depth);
  }

  private static JsonArray arrayToJsonArray(Object o, int depth) {
    Preconditions.checkArgument(o.getClass().isArray());
    final int length = Array.getLength(o);
    final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (int i = 0; i < length; i++) {
      arrayBuilder.add(_toJsonObject(Array.get(o, i), depth));
    }
    return arrayBuilder.build();
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
    Preconditions.checkNotNull(field);
    return Annotations.getJsonNameValueIfAnnotated(field)
            .orElse(field.getName());
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

  private static JsonArray collectionToJsonArray(final Collection<Object> collection, final int currentDepth) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    collection.stream()
            .forEach(element -> jsonArrayBuilder.add(_toJsonObject(element, currentDepth)));
    return jsonArrayBuilder.build();
  }
}
