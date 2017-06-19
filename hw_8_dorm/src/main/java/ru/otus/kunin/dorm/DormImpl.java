package ru.otus.kunin.dorm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class DormImpl implements Dorm {

  private final Connection connection;
  private final SqlGenerator sqlGenerator;
  private final TypeMapper typeMapper;
  private final LoadingCache<Class<? extends DormEntity>, TypeMapping> typeMappingCache;

  public DormImpl(final Connection connection, final TypeMapper typeMapper, final SqlGenerator sqlGenerator) {
    this.connection = connection;
    this.typeMapper = typeMapper;
    this.sqlGenerator = sqlGenerator;
    this.typeMappingCache = CacheBuilder.newBuilder().build(CacheLoader.from(typeMapper::mappingForClass));
  }

  @Override
  public <T extends DormEntity> void createTable(final Class<T> type) throws SQLException {
    TypeMapping typeMapping = typeMappingCache.getUnchecked(type);
    SqlStatement createTable = sqlGenerator.createTable(typeMapping);
    PreparedStatement createStatement = connection.prepareStatement(createTable.getQuery());
    createStatement.execute();
  }

  @Override
  public <T extends DormEntity> void dropTable(final Class<T> type) throws SQLException {
    TypeMapping typeMapping = typeMappingCache.getUnchecked(type);
    SqlStatement dropTable = sqlGenerator.dropTable(typeMapping);
    PreparedStatement dropStatement = connection.prepareStatement(dropTable.getQuery());
    dropStatement.execute();
  }

  @Override
  public <T extends DormEntity> void save(final T value) throws SQLException {
    TypeMapping typeMapping = typeMappingCache.getUnchecked(value.getClass());
    SqlStatement statement = value.isNew() ? sqlGenerator.insert(typeMapping) : sqlGenerator.update(typeMapping);
    try (PreparedStatement preparedStatement = connection.prepareStatement(statement.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
      Map<Integer, FieldMapping> parameterList = statement.getParameterList();
      for (final Map.Entry<Integer, FieldMapping> entry : parameterList.entrySet()) {
        FieldMapping fieldMapping = parameterList.get(entry.getKey());
        preparedStatement.setObject(entry.getKey(), fieldMapping.get(value));
      }
      int numUpdates = preparedStatement.executeUpdate();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        long id = generatedKeys.getLong(1);
        typeMapping.getIdField().set(value, id);
      }
      // TODO(dima) remove logging
      System.out.println("Updated: " + numUpdates + " value: " + value);
    }
  }

  @Override
  public <T extends DormEntity> T load(final long id, final Class<T> clazz) {
    return null;
  }
}
