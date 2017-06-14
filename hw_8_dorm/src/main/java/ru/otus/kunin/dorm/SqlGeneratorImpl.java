package ru.otus.kunin.dorm;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorImpl implements SqlGenerator {

  @Override
  public String createTable(final TypeMapping type) {
    ArrayList<FieldMapping> fieldMappings = Lists.newArrayList(type.getIdField());
    fieldMappings.addAll(type.getNonIdFieldMappings());
    List<String> columns = fieldMappings.stream()
        .map(fm -> fm.getSqlName() + " " + fm.getSqlDefinition()).collect(toList());
    return "create table " + type.getSqlTable() + " (" + Joiner.on(", ").join(columns) + ");";
  }

  @Override
  public String dropTable(final TypeMapping type) {
    return "drop table " + type.getSqlTable() + ";";
  }

  @Override
  public String insert(final TypeMapping type) {
    return null;
  }

  @Override
  public String update(final TypeMapping type) {
    return null;
  }

  @Override
  public String selectOne(final TypeMapping type) {
    return null;
  }

  @Override
  public String seleactAll(final TypeMapping type) {
    return null;
  }
}
