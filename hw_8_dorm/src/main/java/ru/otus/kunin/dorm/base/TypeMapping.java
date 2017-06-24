package ru.otus.kunin.dorm.base;

import ru.otus.kunin.dorm.api.DormEntity;

import java.util.List;

public class TypeMapping {

  private final Class<? extends DormEntity> type;

  private final FieldMapping idField;

  private final String sqlTable;

  private final List<FieldMapping> nonIdFieldMappings;

  TypeMapping(Class<? extends DormEntity> type, FieldMapping idField, String sqlTable, List<FieldMapping> nonIdFieldMappings) {
    this.type = type;
    this.idField = idField;
    this.sqlTable = sqlTable;
    this.nonIdFieldMappings = nonIdFieldMappings;
  }

  public Class<? extends DormEntity> getType() {
    return type;
  }

  public FieldMapping getIdField() {
    return idField;
  }

  public String getSqlTable() {
    return sqlTable;
  }

  public List<FieldMapping> getNonIdFieldMappings() {
    return nonIdFieldMappings;
  }
}
