package ru.otus.kunin.dorm.main;

import ru.otus.kunin.dorm.Connector;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {
    System.out.println("<dorm>");
    try (Connection connection = Connector.connect()) {
      System.out.println("Connected to: " + connection.getMetaData().getURL());
    }
  }

}
