package ru.otus.kunin.dson;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import ru.otus.kunin.dson.tools.IdentitySetImpl;

public class DSON {

  private final ImmutableMap<Class<?>, ConvertToJsonValue> customConverters;

  private DSON(ImmutableMap<Class<?>, ConvertToJsonValue> converters) {
    this.customConverters = converters;
  }

  public static class Builder {

    private Map<Class<?>, ConvertToJsonValue> customConverters;

    public Builder() {
      this.customConverters = Maps.newHashMap();
    }

    public Builder addCustomConverter(Class<?> type, ConvertToJsonValue converter) {
      this.customConverters.put(type, converter);
      return this;
    }

    public DSON build() {
      return new DSON(ImmutableMap.copyOf(customConverters));
    }
  }

  private final static Map<Class<?>, ConvertToJsonValue> PRIMITIVE_CONVERTERS =
      ImmutableMap.<Class<?>, ConvertToJsonValue>builder()
          .put(Float.class, v -> Json.createValue(((Float) v).doubleValue()))
          .put(Double.class, v -> Json.createValue((double) v))
          .put(Byte.class, v -> Json.createValue(((Byte) v).intValue()))
          .put(Short.class, v -> Json.createValue(((Short) v).intValue()))
          .put(Integer.class, v -> Json.createValue((int) v))
          .put(Long.class, v -> Json.createValue((long) v))
          .put(Boolean.class, v -> (Boolean) v ? JsonValue.TRUE : JsonValue.FALSE)
          .build();

  public JsonValue toJsonObject(Object o) {
    return _toJsonObject(o, new IdentitySetImpl());
  }

  private JsonValue _toJsonObject(final Object value, IdentitySet processedObjects) {
    if (processedObjects.contains(value)) {
      throw new DsonException("Found cyclic dependency for object: " + value);
    }
    processedObjects.add(value);

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
      return arrayToJsonArray(value, processedObjects.copy());
    }

    if (value instanceof Collection) {
      return collectionToJsonArray((Collection<Object>) value, processedObjects.copy());
    }

    if (value instanceof Map) {
      return mapToJsonObject((Map<?, ?>) value, processedObjects.copy());
    }

    return objectToJsonObject(value, processedObjects.copy());
  }

  private JsonArray arrayToJsonArray(Object o, IdentitySet depth) {
    Preconditions.checkArgument(o.getClass().isArray());
    final int length = Array.getLength(o);
    final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (int i = 0; i < length; i++) {
      arrayBuilder.add(_toJsonObject(Array.get(o, i), depth));
    }
    return arrayBuilder.build();
  }

  private JsonObject objectToJsonObject(Object o, IdentitySet currentDepth) {
    final Field[] fields = o.getClass().getFields();
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    Arrays.stream(fields)
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .filter(field -> !Modifier.isTransient(field.getModifiers()))
        .forEach(field -> {
          final String name = fieldName(field);
          final JsonValue value = fieldValue(field, o, currentDepth);
          objectBuilder.add(name, value);
        });
    return objectBuilder.build();
  }

  private JsonValue fieldValue(Field field, Object o, IdentitySet processedObjects) {
    try {
      field.setAccessible(true);
      final Object value = field.get(o);
      return _toJsonObject(value, processedObjects);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private String fieldName(Field field) {
    Preconditions.checkNotNull(field);
    return DSONAnnotationsUtil.getJsonNameValueIfAnnotated(field)
        .orElse(field.getName());
  }

  private JsonObject mapToJsonObject(Map<?, ?> o, IdentitySet currentDepth) {
    final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    o.entrySet().forEach(entry -> {
      final String key = String.valueOf(entry.getKey());
      final JsonValue value = _toJsonObject(entry.getValue(), currentDepth);
      objectBuilder.add(key, value);
    });
    return objectBuilder.build();
  }

  private JsonArray collectionToJsonArray(final Collection<Object> collection, final IdentitySet currentDepth) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    collection.stream()
        .forEach(element -> jsonArrayBuilder.add(_toJsonObject(element, currentDepth)));
    return jsonArrayBuilder.build();
  }
}
