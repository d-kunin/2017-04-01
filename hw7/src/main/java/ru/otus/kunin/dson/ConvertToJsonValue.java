package ru.otus.kunin.dson;

import java.util.function.Function;
import javax.json.JsonValue;

@FunctionalInterface
public interface ConvertToJsonValue extends Function<Object, JsonValue> {

}
