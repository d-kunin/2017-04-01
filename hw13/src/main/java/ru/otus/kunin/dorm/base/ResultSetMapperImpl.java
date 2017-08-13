package ru.otus.kunin.dorm.base;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import ru.otus.kunin.dorm.api.DormEntity;
import ru.otus.kunin.dorm.api.DormException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResultSetMapperImpl implements ResultSetMapper {

  @Override
  public <T extends DormEntity> T entityFromResultSet(final ResultSet resultSet, final TypeMapping typeMapping) throws SQLException {
    Preconditions.checkArgument(!resultSet.isAfterLast(), "resultSet.isAfterLast() is true");
    try {
      final T entity = (T) typeMapping.getType().newInstance();
      final ArrayList<FieldMapping> fieldMappings = Lists.newArrayList(typeMapping.getIdField());
      fieldMappings.addAll(typeMapping.getNonIdFieldMappings());
      for (final FieldMapping fieldMapping : fieldMappings) {
        Object value = resultSet.getObject(fieldMapping.getSqlName());
        fieldMapping.set(entity, value);
      }
      return entity;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new DormException("Entity class must have public no argument constructor", e);
    }
  }
}
