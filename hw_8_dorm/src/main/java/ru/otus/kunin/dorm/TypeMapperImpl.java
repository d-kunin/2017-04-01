package ru.otus.kunin.dorm;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import javax.persistence.Table;

public class TypeMapperImpl implements TypeMapper {
  
  private final FieldMapper fieldMapper;

  public TypeMapperImpl(FieldMapper fieldMapper) {
    this.fieldMapper = fieldMapper;
  }

  @Override
  public <T extends DormEntity> TypeMapping mappingForClass(Class<T> clazz) {
    final List<Field> allFields = ReflectionUtils.getAllFields(clazz);
    final List<FieldMapping> fieldMappings = allFields.stream().map(fieldMapper::mapField).collect(toList());
    final List<FieldMapping> idFields = fieldMappings.stream().filter(FieldMapping::isId).collect(toList());
    if (idFields.size() != 1) {
      throw new DormException("There must be exactly one @Id field in an Entity class, found: " + idFields.size());
    }
    final FieldMapping idFieldMapping = idFields.get(0);
    final List<FieldMapping> nonIdFieldMappings = fieldMappings.stream().filter(fm -> !fm.isId()).collect(toList());
    final String tableName = tableNameFor(clazz);
    return new TypeMapping(clazz, idFieldMapping, tableName, nonIdFieldMappings);
  }

  @VisibleForTesting
  String tableNameFor(Class<?> clazz) {
    final Optional<Table> tableAnnotation = Optional.ofNullable(clazz.getAnnotation(Table.class));
    final Optional<String> name = tableAnnotation.filter(t -> !isNullOrEmpty(t.name())).map(Table::name);
    // TODO(dima) pluralise? user -> users
    return name.orElse(clazz.getSimpleName()).toLowerCase();
  }

}
