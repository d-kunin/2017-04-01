package ru.otus.kunin.dorm;

import java.lang.reflect.Field;
import java.util.List;

public class TypeMapping {

  private final Class<? extends DormEntity> type;

  private final Field idField;

  private final String table;

  private final List<FieldMapping> fieldMappings;

  public TypeMapping(Class<? extends DormEntity> type, Field idField, String table, List<FieldMapping> fieldMappings) {
    this.type = type;
    this.idField = idField;
    this.table = table;
    this.fieldMappings = fieldMappings;
  }
}
