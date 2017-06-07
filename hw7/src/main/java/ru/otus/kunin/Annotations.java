package ru.otus.kunin;

import java.lang.reflect.Field;
import java.util.Optional;

public class Annotations {

    private Annotations () {}

    public static Optional<String> getJsonNameValueIfAnnotated(final Field field) {
        final JsonName annotation = field.getAnnotation(JsonName.class);
        return Optional.ofNullable(annotation).map(JsonName::value);
    }
}
