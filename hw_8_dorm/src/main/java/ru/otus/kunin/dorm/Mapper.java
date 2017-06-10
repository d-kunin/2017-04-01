package ru.otus.kunin.dorm;

public interface Mapper {

  <T extends DormEntity> TypeMapping mappingForClass(Class<T> clazz);

}
