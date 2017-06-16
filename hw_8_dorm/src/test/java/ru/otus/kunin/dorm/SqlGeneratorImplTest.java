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
    assertEquals(0, createTable.getParameterList().size());
  }

  @Test
  public void testDropTable() throws Exception {
    SqlStatement dropTable = sqlGenerator.dropTable(typeMapping);
    assertEquals(
        "drop table dima_users;",
        dropTable.getQuery());
    assertEquals(0, dropTable.getParameterList().size());
  }

  @Test
  public void testInsert() throws Exception {
    SqlStatement insert = sqlGenerator.insert(typeMapping);
    assertEquals(
        "insert into dima_users (name, age, displayName) values (?, ?, ?);",
        insert.getQuery());
    assertEquals(3, insert.getParameterList().size());
    assertEquals("name", insert.getParameterList().get(1).getSqlName());
    assertEquals("age", insert.getParameterList().get(2).getSqlName());
    assertEquals("displayName", insert.getParameterList().get(3).getSqlName());
  }

  @Test
  public void testUpdate() throws Exception {
    SqlStatement update = sqlGenerator.update(typeMapping);
    assertEquals(
        "update dima_users set name=?, age=?, displayName=? where id=?;",
        update.getQuery());
    assertEquals(4, update.getParameterList().size());
    assertEquals("name", update.getParameterList().get(1).getSqlName());
    assertEquals("age", update.getParameterList().get(2).getSqlName());
    assertEquals("displayName", update.getParameterList().get(3).getSqlName());
    assertEquals(typeMapping.getIdField(), update.getParameterList().get(4));
  }

  @Test
  public void testSelectOne() throws Exception {
    SqlStatement selectOne = sqlGenerator.selectOne(typeMapping);
    assertEquals(
        "select * from dima_users where id=?;",
        selectOne.getQuery()
    );
    assertEquals(1, selectOne.getParameterList().size());
    assertEquals(typeMapping.getIdField(), selectOne.getParameterList().get(1));
  }

  @Test
  public void testSelectAll() throws Exception {
    SqlStatement selectAll = sqlGenerator.selectAll(typeMapping);
    assertEquals(
        "select * from dima_users;",
        selectAll.getQuery()
    );
    assertEquals(0, selectAll.getParameterList().size());
  }

}