package ru.otus.kunin.dorm;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class SqlStatement {

  private final String query;

  private final Map<Integer, FieldMapping> parameterList;

  public SqlStatement(final String query) {
    this(query, ImmutableMap.of());
  }

  public SqlStatement(final String query, final Map<Integer, FieldMapping> parameterList) {
    this.query = Preconditions.checkNotNull(query);
    this.parameterList = ImmutableMap.copyOf(Preconditions.checkNotNull(parameterList));
  }

  public String getQuery() {
    return query;
  }

  public Map<Integer, FieldMapping> getParameterList() {
    return parameterList;
  }
}
