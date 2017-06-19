package ru.otus.kunin.dorm.main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import ru.otus.kunin.dorm.Connector;
import ru.otus.kunin.dorm.Dorm;
import ru.otus.kunin.dorm.DormImpl;
import ru.otus.kunin.dorm.FieldMapperImpl;
import ru.otus.kunin.dorm.ResultSetMapperImpl;
import ru.otus.kunin.dorm.SqlGeneratorImpl;
import ru.otus.kunin.dorm.TypeMapperImpl;

public class Main {

  public static void main(String[] args) throws SQLException {
    System.out.println("<dorm>");
    try (Connection connection = Connector.createDataSource().getConnection()) {
      final DatabaseMetaData metaData = connection.getMetaData();
      System.out.println("Connected to: " + metaData.getURL() + "\n" +
          "jdbc version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
      Dorm dorm = new DormImpl(
          connection,
          new TypeMapperImpl(new FieldMapperImpl()),
          new SqlGeneratorImpl(),
          new ResultSetMapperImpl());
      try {
        dorm.createTable(User.class);
        User user = new User("dima", 27, "Dzmitry");
        dorm.save(user);
        user.setDisplayName("Dimon");
        user.setAge(39);
        user.setName("Updated Name");
        dorm.save(user);
        System.out.println(dorm.load(-1, User.class));
        System.out.println(dorm.load(user.getId(), User.class).get().equals(user));
        dorm.save(new User("name", 21, "nice display name"));
        System.out.println(dorm.loadAll(User.class));
      } finally {
        dorm.dropTable(User.class);
      }
    }
  }

}
