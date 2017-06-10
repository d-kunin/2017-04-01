package ru.otus.kunin.dorm.main;

import ru.otus.kunin.dorm.Connector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {
    // NOTE: There is not need to register driver since jdbc 4.0
    // see http://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html
    System.out.println("<dorm>");
    try (Connection connection = Connector.createDataSource().getConnection()) {
      final DatabaseMetaData metaData = connection.getMetaData();
      System.out.println("Connected to: " + metaData.getURL() + "\n" +
          "jdbc version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
    }
  }

}
