package ru.otus.kunin.dorm.base;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    List<Class<?>> allClasses = getClassHierarchy(clazz);
    return allClasses.stream()
        .flatMap(c -> Stream.of(c.getDeclaredFields()))
        .collect(toList());
  }

  private static List<Class<?>> getClassHierarchy(Class<?> clazz) {
    List<Class<?>> allClasses = Lists.newArrayList();
    Class<?> currentClass = clazz;
    while (currentClass != null) {
      allClasses.add(currentClass);
      currentClass = currentClass.getSuperclass();
    }
    return allClasses;
  }

  public static Field getFieldByName(final String name, final Class<?> clazz) {
    final List<Class<?>> classHierarchy = getClassHierarchy(clazz);
    for (Class<?> aClass : classHierarchy) {
      try {
        return aClass.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
      }
    }
    throw new RuntimeException(new NoSuchFieldException("Field '" + name + "' does not exist"));
  }

}
