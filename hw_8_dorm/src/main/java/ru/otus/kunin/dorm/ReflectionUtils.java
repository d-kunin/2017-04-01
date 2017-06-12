package ru.otus.kunin.dorm;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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

  public static List<Field> getAllFields(Class<?> clazz) {
    LinkedList<Class<?>> allClasses = Lists.newLinkedList();
    Class<?> currentClass = clazz;
    while (currentClass != null) {
      allClasses.add(currentClass);
      currentClass = currentClass.getSuperclass();
    }
    List<Field> allFields = allClasses.stream()
        .flatMap(c -> Stream.of(c.getDeclaredFields()))
        .collect(toList());
    return allFields;
  }

}
