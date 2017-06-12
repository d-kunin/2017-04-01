package ru.otus.kunin.dorm;

import java.lang.reflect.Field;

public interface FieldMapper {

  FieldMapping mapField(Field field);

}
