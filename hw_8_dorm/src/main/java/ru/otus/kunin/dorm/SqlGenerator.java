package ru.otus.kunin.dorm;

public interface SqlGenerator {

  SqlStatement createTable(TypeMapping type);

  SqlStatement dropTable(TypeMapping type);

  SqlStatement insert(TypeMapping type);

  SqlStatement update(TypeMapping type);

  SqlStatement selectOne(TypeMapping type);

  SqlStatement selectAll(TypeMapping type);

}
