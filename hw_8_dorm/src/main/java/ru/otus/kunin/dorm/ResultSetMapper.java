package ru.otus.kunin.dorm;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper {

  <T extends DormEntity> T entityFromResultSet(ResultSet resultSet, TypeMapping typeMapping) throws SQLException;

}
