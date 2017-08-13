package ru.otus.kunin.dorm.api;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dorm extends Closeable {

  <T extends DormEntity> void createTable(Class<T> type) throws SQLException;

  <T extends DormEntity> void dropTable(Class<T> type) throws SQLException;

  <T extends DormEntity> void save(T value) throws SQLException;

  <T extends DormEntity> Optional<T> load(long id, Class<T> clazz) throws SQLException;

  <T extends DormEntity> List<T> loadAll(Class<T> clazz) throws SQLException;

}
