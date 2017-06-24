package ru.otus.kunin.dorm.base;

import java.lang.reflect.Field;

public interface FieldMapper {

  FieldMapping mapField(Field field);

}
