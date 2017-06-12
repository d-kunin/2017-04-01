package ru.otus.kunin.dorm;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Id;

public class FieldMapperImpl implements FieldMapper {

  @Override
  public FieldMapping mapField(final Field field) {
    boolean isId = field.getAnnotation(Id.class) != null;
    // TODO(dima) if isId then column definition must be disallowed
    Optional<Column> columnAnnotation = Optional.ofNullable(field.getAnnotation(Column.class));
    String sqlName = columnAnnotation.map(ca -> isNullOrEmpty(ca.name()) ? defaultName(field) : ca.name())
        .orElse(defaultName(field));
    String sqlDefinition = columnAnnotation.map(ca -> isNullOrEmpty(ca.columnDefinition()) ? defaultDefinition(field) : ca.columnDefinition())
        .orElse(defaultDefinition(field));
    return new FieldMapping(field, sqlName, sqlDefinition, isId);
  }

  private String defaultDefinition(final Field field) {
    ImmutableMap<Class<?>, String> defaultMapping = ImmutableMap.<Class<?>, String>builder()
        .put(long.class, "bigint not nullable")
        .put(Long.class, "bigint")
        .put(int.class, "int not nullable")
        .put(Integer.class, "int")
        .put(String.class, "text")
        .build();
    return defaultMapping.get(field.getType());
  }

  private String defaultName(final Field field) {
    return field.getName();
  }
}
