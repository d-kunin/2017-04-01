package ru.otus.kunin.dorm.main;

import com.google.common.collect.Sets;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.base.*;
import ru.otus.kunin.dorm.hibernate.DormHibernateImpl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws Exception {
    System.out.println("<dorm>");
    try (Connection connection = Connector.createDataSource().getConnection()) {
      final DatabaseMetaData metaData = connection.getMetaData();
      System.out.println("Connected to: " + metaData.getURL() + "\n" +
          "jdbc version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
//    try (Dorm dorm = makeBaseDorm(connection)) {
      try (Dorm dorm = makeHibernateDorm()) {
        run(dorm);
      }
    }
  }

  private static DormHibernateImpl makeHibernateDorm() {
    return new DormHibernateImpl(
        Sets.newHashSet(UserEntity.class)
    );
  }

  private static DormImpl makeBaseDorm(Connection connection) {
    return new DormImpl(
        connection,
        new TypeMapperImpl(new FieldMapperImpl()),
        new SqlGeneratorImpl(),
        new ResultSetMapperImpl());
  }

  private static void run(Dorm dorm) throws SQLException {
    try {
      dorm.createTable(UserEntity.class);
      UserEntity user = new UserEntity("dima", 27, "Dzmitry");
      dorm.save(user);
      user.setDisplayName("Dimon");
      user.setAge(39);
      user.setName("Updated Name");
      dorm.save(user);
      dorm.save(new UserEntity("name", 21, "nice display name"));
      System.out.println(dorm.loadAll(UserEntity.class));
      System.out.println(dorm.load(-1, UserEntity.class));
      System.out.println(dorm.load(user.getId(), UserEntity.class).get().equals(user));
    } finally {
      dorm.dropTable(UserEntity.class);
    }
  }
}
