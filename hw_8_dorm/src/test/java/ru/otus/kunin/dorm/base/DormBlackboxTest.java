package ru.otus.kunin.dorm.base;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.hibernate.DormHibernateImpl;
import ru.otus.kunin.dorm.main.Connector;
import ru.otus.kunin.dorm.main.UserEntity;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.Assert.*;

public class DormBlackboxTest {

  Connection connection;
  Dorm dorm;

  @Before
  public void setUp() throws Exception {
    connection = Connector.createDataSource().getConnection();
//    dorm = new DormImpl(
//        connection,
//        new TypeMapperImpl(new FieldMapperImpl()),
//        new SqlGeneratorImpl(),
//        new ResultSetMapperImpl());
    dorm = new DormHibernateImpl(Sets.newHashSet(UserEntity.class));
    dorm.createTable(UserEntity.class);
  }

  @After
  public void tearDown() throws Exception {
    dorm.dropTable(UserEntity.class);
    connection.close();
  }

  @Test
  public void testNewUserIsSaved() throws Exception {
    assertEquals(0, dorm.loadAll(UserEntity.class).size());
    UserEntity user = makeUser();
    assertTrue(user.isNew());
    dorm.save(user);
    assertFalse(user.isNew());
    assertEquals(1, dorm.loadAll(UserEntity.class).size());
    assertEquals(user, dorm.load(user.getId(), UserEntity.class).get());
  }

  @Test
  public void testUserIsUpdated() throws Exception {
    assertEquals(0, dorm.loadAll(UserEntity.class).size());
    UserEntity user = makeUser();
    dorm.save(user);
    user.setName("updated_name");
    user.setDisplayName("updated_display_name");
    user.setAge(24);
    dorm.save(user);
    Optional<UserEntity> userFromDb = dorm.load(user.getId(), UserEntity.class);
    assertEquals(user, userFromDb.get());
  }

  @Test
  public void testUserNotFoundIsEmptyOptional() throws Exception {
    assertFalse(dorm.load(1, UserEntity.class).isPresent());
  }

  @Test
  public void testMultipleUsersSaved() throws Exception {
    dorm.save(makeUser());
    dorm.save(makeUser());
    dorm.save(makeUser());
    assertEquals(3, dorm.loadAll(UserEntity.class).size());
  }

  private static UserEntity makeUser() {
    return new UserEntity("user_name", 42, "user_display_name");
  }

}