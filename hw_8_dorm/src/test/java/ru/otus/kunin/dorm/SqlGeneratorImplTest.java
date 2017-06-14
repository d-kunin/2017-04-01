package ru.otus.kunin.dorm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class SqlGeneratorImplTest {

  TypeMapping typeMapping = new TypeMapperImpl(new FieldMapperImpl()).mappingForClass(User.class);
  SqlGeneratorImpl sqlGenerator = new SqlGeneratorImpl();

  @Test
  public void testCreateTable() throws Exception {
    String createTable = sqlGenerator.createTable(typeMapping);
    assertEquals(
        "create table dima_users (id bigint auto_increment primary key, name varchar(255), age int(3) not null default 0, displayName text);",
        createTable);
  }

  @Test
  public void testDropTable() throws Exception {
    String dropTable = sqlGenerator.dropTable(typeMapping);
    assertEquals(
        "drop table dima_users;",
        dropTable);
  }

  @Test
  public void testInsert() throws Exception {
    String insert = sqlGenerator.insert(typeMapping);
    assertEquals(
        "insert into dima_users (name, age, displayName) values (?, ?, ?);",
        insert);
  }

  @Test
  public void testUpdate() throws Exception {
  }

  @Test
  public void testSelectOne() throws Exception {
  }

  @Test
  public void testSeleactAll() throws Exception {
  }

}