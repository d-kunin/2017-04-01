package ru.otus.kunin.dorm.base;

import ru.otus.kunin.dorm.api.DormEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper {

  <T extends DormEntity> T entityFromResultSet(ResultSet resultSet, TypeMapping typeMapping) throws SQLException;

}
