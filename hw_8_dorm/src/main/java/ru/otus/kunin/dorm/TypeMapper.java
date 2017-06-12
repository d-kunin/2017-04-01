package ru.otus.kunin.dorm;

public interface TypeMapper {

  <T extends DormEntity> TypeMapping mappingForClass(Class<T> clazz);

}
