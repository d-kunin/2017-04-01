package ru.otus.kunin.dorm.main;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zaxxer.hikari.HikariDataSource;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.base.*;
import ru.otus.kunin.dorm.hibernate.DormHibernateImpl;
import ru.otus.kunin.dorm.main.entity.AddressEntity;
import ru.otus.kunin.dorm.main.entity.PhoneEntity;
import ru.otus.kunin.dorm.main.entity.UserEntity;
import ru.otus.kunin.dorm.main.entity.UserWithAddressAndPhoneEntity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws Exception {
    System.out.println("<dorm>");
    try (HikariDataSource dataSource = Connector.createHikariDataSource()) {
      final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
      System.out.println("Connected to: " + metaData.getURL() + "\n" +
          "jdbc version: " + metaData.getJDBCMajorVersion() + "." + metaData.getJDBCMinorVersion());
      Lists.newArrayList(
          makeBaseDorm(dataSource.getConnection()),
          makeHibernateDorm())
          .forEach(d -> {
            try (Dorm dorm = d) {
              run(dorm);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
    }
  }

  private static DormHibernateImpl makeHibernateDorm() {
    return new DormHibernateImpl(
        Sets.newHashSet(
            UserEntity.class,
            AddressEntity.class,
            PhoneEntity.class,
            UserWithAddressAndPhoneEntity.class));
  }

  private static DormImpl makeBaseDorm(Connection connection) {
    return new DormImpl(
        connection,
        new TypeMapperImpl(new FieldMapperImpl()),
        new SqlGeneratorImpl(),
        new ResultSetMapperImpl());
  }

  private static void run(Dorm dorm) throws SQLException {
    dropTablesSilent(dorm);
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
  }

  private static void dropTablesSilent(Dorm dorm) {
    try {
      dorm.dropTable(UserEntity.class);
    } catch (Exception ignored) {
    }
  }
}
