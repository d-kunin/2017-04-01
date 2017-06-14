package ru.otus.kunin.dorm;

public interface SqlGenerator {

  String createTable(TypeMapping type);

  String dropTable(TypeMapping type);

  String insert(TypeMapping type);

  String update(TypeMapping type);

  String selectOne(TypeMapping type);

  String seleactAll(TypeMapping type);

}
