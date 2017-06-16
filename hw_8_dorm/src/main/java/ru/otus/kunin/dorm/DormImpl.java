package ru.otus.kunin.dorm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DormImpl implements Dorm {

  private final Connection connection;
  private final SqlGenerator sqlGenerator;
  private final TypeMapper typeMapper;
  private final LoadingCache<Class<? extends DormEntity>, TypeMapping> mappingCache;

  public DormImpl(final Connection connection, final TypeMapper typeMapper, final SqlGenerator sqlGenerator) {
    this.connection = connection;
    this.typeMapper = typeMapper;
    this.sqlGenerator = sqlGenerator;
    this.mappingCache = CacheBuilder.newBuilder().build(CacheLoader.from(typeMapper::mappingForClass));
  }

  @Override
  public <T extends DormEntity> void createTable(final Class<T> type) throws SQLException {
    TypeMapping typeMapping = mappingCache.getUnchecked(type);
    SqlStatement createTable = sqlGenerator.createTable(typeMapping);
    PreparedStatement createStatement = connection.prepareStatement(createTable.getQuery());
    createStatement.execute();
  }

  @Override
  public <T extends DormEntity> void dropTable(final Class<T> type) throws SQLException {
    TypeMapping typeMapping = mappingCache.getUnchecked(type);
    SqlStatement dropTable = sqlGenerator.dropTable(typeMapping);
    PreparedStatement dropStatement = connection.prepareStatement(dropTable.getQuery());
    dropStatement.execute();
  }

  @Override
  public <T extends DormEntity> void save(final T value) {

  }

  @Override
  public <T extends DormEntity> T load(final long id, final Class<T> clazz) {
    return null;
  }
}
