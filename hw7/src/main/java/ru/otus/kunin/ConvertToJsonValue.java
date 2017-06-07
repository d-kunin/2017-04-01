package ru.otus.kunin;

import javax.json.JsonValue;
import java.util.function.Function;

@FunctionalInterface
public interface ConvertToJsonValue extends Function<Object, JsonValue> {
}
