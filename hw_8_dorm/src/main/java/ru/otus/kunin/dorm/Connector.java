package ru.otus.kunin.dorm;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {

  private static final String USER = "tully";
  private static final String PASSWORD = "tully";
  private static final int PORT = 3306;
  private static final String SERVER_NAME = "localhost";
  private static final String DB_NAME = "db_example";

  public static Connection connectWithDriverManager() throws SQLException {
    Properties connectionProperties = new Properties();
    connectionProperties.put("user", USER);
    connectionProperties.put("password", PASSWORD);
    connectionProperties.put("useSSL", false);
    return DriverManager.getConnection("jdbc:mysql://localhost:"+ PORT + "/" + DB_NAME, connectionProperties);
  }

  public static DataSource createDataSource() {
    final MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setUser(USER);
    mysqlDataSource.setPassword(PASSWORD);
    mysqlDataSource.setPort(PORT);
    mysqlDataSource.setServerName(SERVER_NAME);
    mysqlDataSource.setDatabaseName(DB_NAME);
    return mysqlDataSource;
  }

}
