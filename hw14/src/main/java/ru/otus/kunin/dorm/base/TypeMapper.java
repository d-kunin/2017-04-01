package ru.otus.kunin.dorm.base;

import ru.otus.kunin.dorm.api.DormEntity;

public interface TypeMapper {

  <T extends DormEntity> TypeMapping mappingForClass(Class<T> clazz);

}
