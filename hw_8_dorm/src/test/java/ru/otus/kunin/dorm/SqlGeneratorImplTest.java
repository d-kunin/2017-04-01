package ru.otus.kunin.dorm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class SqlGeneratorImplTest {

  TypeMapping typeMapping = new TypeMapperImpl(new FieldMapperImpl()).mappingForClass(User.class);
  SqlGeneratorImpl sqlGenerator = new SqlGeneratorImpl();

  @Test
  public void testCreateTable() throws Exception {
    SqlStatement createTable = sqlGenerator.createTable(typeMapping);
    assertEquals(
        "create table dima_users (id bigint auto_increment primary key, name varchar(255), age int(3) not null default 0, displayName text);",
        createTable.getQuery());
  }

  @Test
  public void testDropTable() throws Exception {
    SqlStatement dropTable = sqlGenerator.dropTable(typeMapping);
    assertEquals(
        "drop table dima_users;",
        dropTable.getQuery());
  }

  @Test
  public void testInsert() throws Exception {
    SqlStatement insert = sqlGenerator.insert(typeMapping);
    assertEquals(
        "insert into dima_users (name, age, displayName) values (?, ?, ?);",
        insert.getQuery());
  }

  @Test
  public void testUpdate() throws Exception {
    SqlStatement update = sqlGenerator.update(typeMapping);
    assertEquals(
        "update dima_users set name=?, age=?, displayName=? where id=?;",
        update.getQuery());
  }

  @Test
  public void testSelectOne() throws Exception {
    SqlStatement selectOne = sqlGenerator.selectOne(typeMapping);
    assertEquals(
        "select * from dima_users where id=?;",
        selectOne.getQuery()
    );
  }

  @Test
  public void testSelectAll() throws Exception {
    SqlStatement selectAll = sqlGenerator.selectAll(typeMapping);
    assertEquals(
        "select * from dima_users;",
        selectAll.getQuery()
    );
  }

}