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

  private final static Map<Class<?>, Converter> PRIMITIVE_CONVERTERS = ImmutableMap.<Class<?>, Converter>builder()
      .put(Double.class,  v -> Json.createValue((double) v))
      .put(Float.class,   v -> Json.createValue((double) v))
      .put(Byte.class,    v -> Json.createValue((int) v))
      .put(Short.class,   v -> Json.createValue((int) v))
      .put(Integer.class, v -> Json.createValue((int) v))
      .put(Long.class,    v -> Json.createValue((long) v))
      .put(Boolean.class, v -> (Boolean)v ? JsonValue.TRUE : JsonValue.FALSE)
      .build();

  public static JsonValue toJsonObject(Object o) {
    if (null == o) {
      return JsonValue.NULL;
    }

    if (PRIMITIVE_CONVERTERS.containsKey(o.getClass())) {
      return primitiveToJsonObject(o);
    }

    if (o.getClass().isArray() || o instanceof Collection) {
      return arrayToJsonObject(o);
    }

    if (o instanceof CharSequence) {
      return Json.createValue(o.toString());
    }

    // TODO
    return JsonValue.EMPTY_JSON_OBJECT;
  }

  private static JsonArray arrayToJsonObject(final Object o) {
    final Object[] asArray = o instanceof Collection<?> ? ((Collection<?>)o).toArray() : (Object[])o;
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    Stream.of(asArray).forEach(element -> {
      jsonArrayBuilder.add(toJsonObject(element));
    });
    return jsonArrayBuilder.build();
  }

  @FunctionalInterface
  interface Converter extends Function<Object, JsonValue> {

  }

  private static JsonValue primitiveToJsonObject(final Object o) {
    return PRIMITIVE_CONVERTERS.get(o.getClass()).apply(o);
  }

}