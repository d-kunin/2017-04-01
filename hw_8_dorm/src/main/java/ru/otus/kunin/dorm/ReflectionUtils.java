package ru.otus.kunin.dorm;

import java.lang.reflect.Field;

public class ReflectionUtils {

  public static void setFieldValue(Field field, Object instance, Object value) {
    field.setAccessible(true);
    try {
      field.set(instance, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object getFieldValue(Field field, Object instance) {
    field.setAccessible(true);
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
