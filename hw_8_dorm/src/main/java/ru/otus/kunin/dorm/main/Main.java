package ru.otus.kunin.dorm.main;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import ru.otus.kunin.dorm.Connector;
import ru.otus.kunin.dorm.Dorm;
import ru.otus.kunin.dorm.DormImpl;
import ru.otus.kunin.dorm.FieldMapperImpl;
import ru.otus.kunin.dorm.SqlGeneratorImpl;
import ru.otus.kunin.dorm.TypeMapperImpl;

public class Main {

  public static void main(String[] args) throws SQLException {
    // NOTE: There is not need to register driver since jdbc 4.0
    // see http://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html

    // TODO(dima) read on on the SQL exceptions http://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
    System.out.println("<dorm>");
    try (Connection connection = Connector.createDataSource().getConnection()) {
      final DatabaseMetaData metaData = connection.getMetaData();
      System.out.println("Connected to: " + metaData.getURL() + "\n" +
          "jdbc version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
      Dorm dorm = new DormImpl(
          connection,
          new TypeMapperImpl(new FieldMapperImpl()),
          new SqlGeneratorImpl()
      );
      try {
        dorm.createTable(User.class);
        User user = new User("dima", 27, "Dzmitry");
        dorm.save(user);
        user.setDisplayName("Dimon");
        dorm.save(user);
      } finally {
        dorm.dropTable(User.class);
      }
    }
  }

}
