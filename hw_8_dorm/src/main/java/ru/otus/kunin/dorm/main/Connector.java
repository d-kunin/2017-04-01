package ru.otus.kunin.dorm.main;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

public class Connector {

  private static final String USER = "tully";
  private static final String PASSWORD = "tully";
  private static final int PORT = 3306;
  private static final String SERVER_NAME = "localhost";
  private static final String DB_NAME = "db_example";

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
