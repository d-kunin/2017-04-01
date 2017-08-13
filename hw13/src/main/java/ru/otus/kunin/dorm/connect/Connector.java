package ru.otus.kunin.dorm.connect;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

  private static final String USER = "tully";
  private static final String PASSWORD = "tully";
  private static final int PORT = 3306;
  private static final String SERVER_NAME = "localhost";
  private static final String DB_NAME = "db_example";
  private static final String JDBC_URL = "jdbc:mysql://" + SERVER_NAME + ":" + PORT + "/" + DB_NAME;

  static {
    try {
      // HACK!!! Tomcat does not like when driver is not
      // loaded explicitly.
      DriverManager.registerDriver(new com.mysql.jdbc.Driver());
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }
  }

  public static HikariDataSource createHikariDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(JDBC_URL);
    config.setUsername(USER);
    config.setPassword(PASSWORD);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    return new HikariDataSource(config);
  }

}
