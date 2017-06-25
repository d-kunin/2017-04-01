package ru.otus.kunin.dorm.base;

import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.hibernate.DormHibernateImpl;
import ru.otus.kunin.dorm.main.AddressEntity;
import ru.otus.kunin.dorm.main.Connector;
import ru.otus.kunin.dorm.main.UserEntity;
import ru.otus.kunin.dorm.main.UserWithAddressAndPhoneEntity;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DormBlackboxTest {

  interface DormFactory extends Supplier<Dorm> {
  }

  static class DormParameter {
    public final DormFactory factory;
    public final String name;

    DormParameter(DormFactory factory, String name) {
      this.factory = factory;
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  @Parameters(name = "{0}")
  public static DormParameter[] data() throws SQLException {
    return new DormParameter[]{
        new DormParameter(() -> {
          try {
            return new DormImpl(
                Connector.createDataSource().getConnection(),
                new TypeMapperImpl(new FieldMapperImpl()),
                new SqlGeneratorImpl(),
                new ResultSetMapperImpl());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        },
            "DormImpl"),
        new DormParameter(
            () -> new DormHibernateImpl(ImmutableSet.of(
                UserEntity.class,
                UserWithAddressAndPhoneEntity.class,
                AddressEntity.class
            )),
            "DormHibernateImpl")};
  }

  @Parameter
  public DormParameter dormParameter;
  private Dorm dorm;

  @Before
  public void setUp() throws Exception {
    dorm = dormParameter.factory.get();
    try {
      dorm.dropTable(UserEntity.class);
    } catch (Exception ignored) {
    }
    dorm.createTable(UserEntity.class);
  }

  @After
  public void tearDown() throws Exception {
    dorm.dropTable(UserEntity.class);
    dorm.close();
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