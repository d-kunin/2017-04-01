package ru.otus.kunin.dorm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class SqlGeneratorImplTest {

  TypeMapping typeMapping = new TypeMapperImpl(new FieldMapperImpl()).mappingForClass(User.class);
  SqlGeneratorImpl sqlGenerator = new SqlGeneratorImpl();

  @Test
  public void createTable() throws Exception {
    String createTable = sqlGenerator.createTable(typeMapping);
    assertEquals(
        "create table dima_users (id bigint auto_increment primary key, name varchar(255), age int(3) not null default 0, displayName text);",
        createTable);
  }

  @Test
  public void dropTable() throws Exception {
    String dropTable = sqlGenerator.dropTable(typeMapping);
    assertEquals(
        "drop table dima_users;",
        dropTable);
  }

  @Test
  public void insert() throws Exception {
  }

  @Test
  public void update() throws Exception {
  }

  @Test
  public void selectOne() throws Exception {
  }

  @Test
  public void seleactAll() throws Exception {
  }

}