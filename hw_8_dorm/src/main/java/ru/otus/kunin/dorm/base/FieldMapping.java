package ru.otus.kunin.dorm.base;

import java.lang.reflect.Field;

public class FieldMapping {

  private final Field field;
  private final String sqlName;
  private final String sqlDefinition;
  private final boolean isId;


  FieldMapping(Field field, final String sqlName, String sqlDefinition, final boolean isId) {
    this.field = field;
    this.sqlName = sqlName;
    this.sqlDefinition = sqlDefinition;
    this.isId = isId;
  }

  public Object get(Object instance) {
    return ReflectionUtils.getFieldValue(field, instance);
  }

  public void set(Object instance, Object value) {
    ReflectionUtils.setFieldValue(field, instance, value);
  }

  public Field getField() {
    return field;
  }

  public String getSqlName() {
    return sqlName;
  }

  public String getSqlDefinition() {
    return sqlDefinition;
  }

  public boolean isId() {
    return isId;
  }
}
