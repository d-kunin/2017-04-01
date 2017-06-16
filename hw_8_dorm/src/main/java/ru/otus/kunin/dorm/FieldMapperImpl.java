package ru.otus.kunin.dorm;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Id;

public class FieldMapperImpl implements FieldMapper {

  private final static ImmutableMap<Class<?>, String> DEFAULT_TYPE_MAPPING = ImmutableMap.<Class<?>, String>builder()
      // TODO(dima) add more mappings
      .put(long.class, "bigint not nullable")
      .put(Long.class, "bigint")
      .put(int.class, "int not nullable")
      .put(Integer.class, "int")
      .put(String.class, "text")
      .build();

  @VisibleForTesting final static String ID_FIELD_NAME = "id";
  @VisibleForTesting final static String ID_FIELD_DEFINITION = "bigint auto_increment primary key";

  @Override
  public FieldMapping mapField(final Field field) {
    boolean isId = field.getAnnotation(Id.class) != null;
    String sqlName = isId ? ID_FIELD_NAME : nameFor(field);
    String sqlDefinition = isId ? ID_FIELD_DEFINITION : columnDefinitionFor(field);
    return new FieldMapping(field, sqlName, sqlDefinition, isId);
  }

  @VisibleForTesting
  String columnDefinitionFor(final Field field) {
    Optional<Column> columnAnnotation = Optional.ofNullable(field.getAnnotation(Column.class));
    final Optional<String> definition = columnAnnotation
        .filter(ca -> !isNullOrEmpty(ca.columnDefinition())).map(Column::columnDefinition);
    return definition.orElse(defaultMappingForType(field.getType()));
  }

  @VisibleForTesting
  String defaultMappingForType(final Class<?> type) {
    return DEFAULT_TYPE_MAPPING.get(type);
  }

  @VisibleForTesting
  String nameFor(final Field field) {
    Optional<Column> columnAnnotation = Optional.ofNullable(field.getAnnotation(Column.class));
    final Optional<String> name = columnAnnotation.filter(ca -> !isNullOrEmpty(ca.name())).map(Column::name);
    return name.orElse(field.getName());
  }
}
