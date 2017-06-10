package ru.otus.kunin.dorm;

import java.lang.reflect.Field;

public class FieldMapping {

  private final Field field;
  private final String sqlDefinition;


  public FieldMapping(Field field, String sqlDefinition) {
    this.field = field;
    this.sqlDefinition = sqlDefinition;
  }

  public Object get(Object instance) {
    return ReflectionUtils.getFieldValue(field, instance);
  }

  public void set(Object instance, Object value) {
    ReflectionUtils.setFieldValue(field, instance, value);
  }
}
