package ru.otus.kunin.dorm.main;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Connector {

  private static final String USER = "tully";
  private static final String PASSWORD = "tully";
  private static final int PORT = 3306;
  private static final String SERVER_NAME = "localhost";
  private static final String DB_NAME = "db_example";
  private static final String JDBC_URL = "jdbc:mysql://" + SERVER_NAME + ":" +  PORT + "/" + DB_NAME;

  public static MysqlDataSource createMySqlDataSource() {
    final MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setUser(USER);
    mysqlDataSource.setPassword(PASSWORD);
    mysqlDataSource.setPort(PORT);
    mysqlDataSource.setServerName(SERVER_NAME);
    mysqlDataSource.setDatabaseName(DB_NAME);
    return mysqlDataSource;
  }

  public static HikariDataSource createHikariDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(JDBC_URL);
    config.setUsername(USER);
    config.setPassword(PASSWORD);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    return  new HikariDataSource(config);
  }

}
