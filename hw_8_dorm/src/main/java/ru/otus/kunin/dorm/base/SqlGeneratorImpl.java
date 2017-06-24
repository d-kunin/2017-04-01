package ru.otus.kunin.dorm.base;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlGeneratorImpl implements SqlGenerator {

  @Override
  public SqlStatement createTable(final TypeMapping type) {
    ArrayList<FieldMapping> fieldMappings = Lists.newArrayList(type.getIdField());
    fieldMappings.addAll(type.getNonIdFieldMappings());
    List<String> columns = fieldMappings.stream()
        .map(fm -> fm.getSqlName() + " " + fm.getSqlDefinition())
        .collect(toList());
    String query = "create table " + type.getSqlTable() + " " +
               "(" + Joiner.on(", ").join(columns) + ");";
    return new SqlStatement(query);
  }

  @Override
  public SqlStatement dropTable(final TypeMapping type) {
    return new SqlStatement("drop table " + type.getSqlTable() + ";");
  }

  @Override
  public SqlStatement insert(final TypeMapping type) {
    List<FieldMapping> nonIdFieldMappings = type.getNonIdFieldMappings();
    List<String> columnNames = nonIdFieldMappings.stream()
        .map(FieldMapping::getSqlName)
        .collect(toList());
    String[] valuesPlaceholders = new String[nonIdFieldMappings.size()];
    Arrays.fill(valuesPlaceholders, "?");
    String query = "insert into " + type.getSqlTable() + " " +
               "(" + Joiner.on(", ").join(columnNames) + ") " +
               "values (" + Joiner.on(", ").join(valuesPlaceholders) + ");";
    ImmutableMap.Builder<Integer, FieldMapping> mapBuilder = ImmutableMap.builder();
    for (int i = 0; i < nonIdFieldMappings.size(); i++) {
      mapBuilder.put(i + 1, nonIdFieldMappings.get(i));
    }
    return new SqlStatement(query, mapBuilder.build());
  }

  @Override
  public SqlStatement update(final TypeMapping type) {
    FieldMapping idField = type.getIdField();
    List<FieldMapping> nonIdFieldMappings = type.getNonIdFieldMappings();
    List<String> columnsPlusPlaceholders = type.getNonIdFieldMappings().stream()
        .map(fm -> fm.getSqlName() + "=?")
        .collect(toList());
    String idPlusPlaceholder = idField.getSqlName() + "=?";
    String query = "update " + type.getSqlTable() + " " +
               "set " + Joiner.on(", ").join(columnsPlusPlaceholders) + " " +
               "where " + idPlusPlaceholder + ";";
    ImmutableMap.Builder<Integer, FieldMapping> mapBuilder = ImmutableMap.builder();
    for (int i = 0; i < nonIdFieldMappings.size(); i++) {
      mapBuilder.put(i + 1, nonIdFieldMappings.get(i));
    }
    mapBuilder.put(nonIdFieldMappings.size() + 1, type.getIdField());
    return new SqlStatement(query, mapBuilder.build());
  }

  @Override
  public SqlStatement selectOne(final TypeMapping type) {
    String query = "select * from " + type.getSqlTable() + " " +
               "where " + type.getIdField().getSqlName() + "=?;";
    ImmutableMap<Integer, FieldMapping> parameters = ImmutableMap.of(1, type.getIdField());
    return new SqlStatement(query, parameters);
  }

  @Override
  public SqlStatement selectAll(final TypeMapping type) {
    return new SqlStatement("select * from " + type.getSqlTable() + ";");
  }
}
