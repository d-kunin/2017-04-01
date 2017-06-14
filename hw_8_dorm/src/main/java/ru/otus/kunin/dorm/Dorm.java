package ru.otus.kunin.dorm;

import java.sql.SQLException;

public interface Dorm {

  <T extends DormEntity> void createTable(Class<T> type) throws SQLException;

  <T extends DormEntity> void dropTable(Class<T> type) throws SQLException;

  <T extends DormEntity> void save(T value);

  <T extends DormEntity> T load(long id, Class<T> clazz);

}
