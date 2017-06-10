package ru.otus.kunin.dorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {

  public static Connection connect() throws SQLException {
    Properties connectionProperties = new Properties();
    connectionProperties.put("user", "tully");
    connectionProperties.put("password", "tully");
    connectionProperties.put("useSSL", false);
    return DriverManager.getConnection("jdbc:mysql://localhost:3306/db_example", connectionProperties);
  }

}
