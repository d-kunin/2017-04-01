package ru.otus.kunin.dson;

import java.lang.reflect.Field;
import java.util.Optional;

class DSONAnnotationsUtil {

  private DSONAnnotationsUtil() {
  }

  static Optional<String> getJsonNameValueIfAnnotated(final Field field) {
    final JsonName annotation = field.getAnnotation(JsonName.class);
    return Optional.ofNullable(annotation).map(JsonName::value);
  }
}
