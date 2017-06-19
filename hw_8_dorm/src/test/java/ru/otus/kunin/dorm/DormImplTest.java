package ru.otus.kunin.dorm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.otus.kunin.dorm.main.User;

public class DormImplTest {

  Connection connection;
  DormImpl dorm;

  @Before
  public void setUp() throws Exception {
    connection = Connector.createDataSource().getConnection();
    dorm = new DormImpl(
        connection,
        new TypeMapperImpl(new FieldMapperImpl()),
        new SqlGeneratorImpl(),
        new ResultSetMapperImpl());
    dorm.createTable(User.class);
  }

  @After
  public void tearDown() throws Exception {
    dorm.dropTable(User.class);
    connection.close();
  }

  @Test
  public void testNewUserIsSaved() throws Exception {
    assertEquals(0, dorm.loadAll(User.class).size());
    User user = makeUser();
    assertTrue(user.isNew());
    dorm.save(user);
    assertFalse(user.isNew());
    assertEquals(1, dorm.loadAll(User.class).size());
    assertEquals(user, dorm.load(user.getId(), User.class).get());
  }

  @Test
  public void testUserIsUpdated() throws Exception {
    assertEquals(0, dorm.loadAll(User.class).size());
    User user = makeUser();
    dorm.save(user);
    user.setName("updated_name");
    user.setDisplayName("updated_display_name");
    user.setAge(24);
    dorm.save(user);
    Optional<User> userFromDb = dorm.load(user.getId(), User.class);
    assertEquals(user, userFromDb.get());
  }

  @Test
  public void testUserNotFoundIsEmptyOptional() throws Exception {
    assertFalse(dorm.load(1, User.class).isPresent());
  }

  @Test
  public void testMultipleUsersSaved() throws Exception {
    dorm.save(makeUser());
    dorm.save(makeUser());
    dorm.save(makeUser());
    assertEquals(3, dorm.loadAll(User.class).size());
  }

  private static User makeUser() {
    return new User("user_name", 42, "user_display_name");
  }

}