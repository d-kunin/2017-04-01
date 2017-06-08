package ru.otus.kunin.dson;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.json.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DSON {

  private final int maxDepth;
  private final Map<Class<?>, ConvertToJsonValue> customConverters;

  private DSON(int maxDepth, Map<Class<?>, ConvertToJsonValue> converters) {
    this.maxDepth = maxDepth;
    this.customConverters = converters;
  }

  public static DSON create(Config config) {
    return new DSON(
            config.maxDepth(),
            ImmutableMap.copyOf(Optional.ofNullable(config.customConverters()).orElse(Collections.EMPTY_MAP)));
  }

  @AutoValue
  public abstract static class Config {

    public abstract int maxDepth();
    public abstract Map<Class<?>, ConvertToJsonValue> customConverters();

    public static Builder builder() {
      return new AutoValue_DSON_Config.Builder()
              .setMaxDepth(DEFAULT_MAX_JSON_DEPTH)
              .setCustomConverters(ImmutableMap.of());
    }

    @AutoValue.Builder
    public abstract static class Builder {
      public abstract Builder setMaxDepth(int maxDepth);
      public abstract Builder setCustomConverters(Map<Class<?>, ConvertToJsonValue> converters);

      abstract Config autoBuild();

      public Config build() {
        final Config config = autoBuild();
        Preconditions.checkArgument(config.maxDepth() >= MIN_MAX_JSON_DEPTH,
                "maxDepth should at least be " + MIN_MAX_JSON_DEPTH);
        return config;
      }
    }
  }

  private final static int MIN_MAX_JSON_DEPTH = 1;

  private final static int DEFAULT_MAX_JSON_DEPTH = 32;

  private final static Map<Class<?>, ConvertToJsonValue> PRIMITIVE_CONVERTERS = ImmutableMap.<Class<?>, ConvertToJsonValue>builder()
      .put(Float.class,   v -> Json.createValue(((Float) v).doubleValue()))
      .put(Double.class,  v -> Json.createValue((double) v))
      .put(Byte.class,    v -> Json.createValue(((Byte)v).intValue()))
      .put(Short.class,   v -> Json.createValue(((Short)v).intValue()))
      .put(Integer.class, v -> Json.createValue((int) v))
      .put(Long.class,    v -> Json.createValue((long) v))
      .put(Boolean.class, v -> (Boolean)v ? JsonValue.TRUE : JsonValue.FALSE)
      .build();

  public JsonValue toJsonObject(Object o) {
    return _toJsonObject(o, 0);
  }

  private JsonValue _toJsonObject(final Object value, int depth) {
    if (depth > maxDepth) {
      //TODO(dima) custom strategies maybe? throw or ignore
      return JsonValue.NULL;
    }
    depth++;

    if (null == value) {
      return JsonValue.NULL;
    }

    final Class<?> valueClass = value.getClass();
    if (customConverters.containsKey(valueClass)) {
      return customConverters.get(valueClass).apply(value);
    }

    if (value instanceof CharSequence) {
      return Json.createValue(value.toString());
    }

    if (PRIMITIVE_CONVERTERS.containsKey(valueClass)) {
      return PRIMITIVE_CONVERTERS.get(valueClass).apply(value);
    }

    if (valueClass.isArray()) {
      return arrayToJsonArray(value, depth);
    }

    if (value instanceof Collection) {
      return collectionToJsonArray((Collection<Object>) value, depth);
    }

    if (value instanceof Map) {
      return mapToJsonObject((Map<?,?>)value, depth);
    }

    return objectToJsonObject(value, depth);
  }

  private JsonArray arrayToJsonArray(Object o, int depth) {
    Preconditions.checkArgument(o.getClass().isArray());
    final int length = Array.getLength(o);
    final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (int i = 0; i < length; i++) {
      arrayBuilder.add(_toJsonObject(Array.get(o, i), depth));
    }
    return arrayBuilder.build();
  }

  private JsonObject objectToJsonObject(Object o, int currentDepth) {
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

  private JsonValue filedValue(Field field, Object o, int currentDepth) {
    try {
      field.setAccessible(true);
      final Object value = field.get(o);
      return _toJsonObject(value, currentDepth);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private String fieldToName(Field field) {
    Preconditions.checkNotNull(field);
    return Annotations.getJsonNameValueIfAnnotated(field)
            .orElse(field.getName());
  }

  private JsonObject mapToJsonObject(Map<?, ?> o, int currentDepth) {
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    o.entrySet().forEach(entry -> {
      final String key = String.valueOf(entry.getKey());
      final JsonValue value = _toJsonObject(entry.getValue(), currentDepth);
      objectBuilder.add(key, value);
    });
    return objectBuilder.build();
  }

  private JsonArray collectionToJsonArray(final Collection<Object> collection, final int currentDepth) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    collection.stream()
            .forEach(element -> jsonArrayBuilder.add(_toJsonObject(element, currentDepth)));
    return jsonArrayBuilder.build();
  }
}
