package ru.otus.kunin.dorm.base;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.hibernate.DormHibernateImpl;
import ru.otus.kunin.dorm.main.Connector;
import ru.otus.kunin.dorm.main.entity.AddressEntity;
import ru.otus.kunin.dorm.main.entity.PhoneEntity;
import ru.otus.kunin.dorm.main.entity.UserEntity;
import ru.otus.kunin.dorm.main.entity.UserWithAddressAndPhoneEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DormBlackboxTest {

  interface DormFactory extends Supplier<Dorm> {
  }

  static class DormTestParameter {
    public final DormFactory factory;
    public final String name;

    DormTestParameter(DormFactory factory, String name) {
      this.factory = factory;
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  @Parameters(name = "{0}")
  public static DormTestParameter[] data() throws SQLException {
    return new DormTestParameter[]{
        new DormTestParameter(
            () -> {
              try {
                return new DormImpl(
                    sHikariDataSource.getConnection(),
                    new TypeMapperImpl(new FieldMapperImpl()),
                    new SqlGeneratorImpl(),
                    new ResultSetMapperImpl());
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            },
            "DormImpl"),
        new DormTestParameter(
            () -> new DormHibernateImpl(ImmutableSet.of(
                UserEntity.class,
                AddressEntity.class,
                PhoneEntity.class,
                UserWithAddressAndPhoneEntity.class
            )),
            "DormHibernateImpl"),
        new DormTestParameter(
            () -> {
              try {
                return new CachingDorm(new DormImpl(
                    sHikariDataSource.getConnection(),
                    new TypeMapperImpl(new FieldMapperImpl()),
                    new SqlGeneratorImpl(),
                    new ResultSetMapperImpl()));
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            },
            "CachedDorm")};
  }

  @Parameter
  public DormTestParameter dormTestParameter;
  private Dorm dorm;

  @Before
  public void setUp() throws Exception {
    dorm = dormTestParameter.factory.get();
    dropTablesSilent();
    dorm.createTable(UserEntity.class);
    dorm.createTable(UserWithAddressAndPhoneEntity.class);
  }

  @After
  public void tearDown() throws Exception {
    dorm.close();
    dropTablesSilent();
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
  public void testMultipleUsersSavedAndLoaded() throws Exception {
    dorm.save(makeUser());
    dorm.save(makeUser());
    dorm.save(makeUser());
    assertEquals(3, dorm.loadAll(UserEntity.class).size());
  }

  @Test
  public void testNewComplexUserIsSavedAndLoaded() throws Exception {
    assertEquals(0, dorm.loadAll(UserWithAddressAndPhoneEntity.class).size());
    UserWithAddressAndPhoneEntity user = makeComplexUserEntity();
    assertTrue(user.isNew());
    dorm.save(user);
    assertFalse(user.isNew());
    assertEquals(1, dorm.loadAll(UserWithAddressAndPhoneEntity.class).size());
    final UserWithAddressAndPhoneEntity loaded = dorm.load(user.getId(), UserWithAddressAndPhoneEntity.class).get();
    assertEquals(user, loaded);
  }

  @Test
  public void testPerformanceLoadAndSaveSimpleEntity() throws Exception {
    final int size = 25;
    final ArrayList<UserEntity> users = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      final UserEntity user = makeUser();
      users.add(user);
      dorm.save(user);
    }
    for (int i = 0; i < size; i++) {
      for (final UserEntity user : users) {
        final UserEntity loadedUser = dorm.load(user.getId(), user.getClass()).get();
        loadedUser.setAge(loadedUser.getAge() + 1);
        dorm.save(loadedUser);
      }
    }
  }

  private static UserEntity makeUser() {
    return new UserEntity("user_name", 42, "user_display_name");
  }

  private static AddressEntity makeAddress() {
    return new AddressEntity("Anders Reimers", 11750);
  }

  private static PhoneEntity makePhone() {
    return new PhoneEntity(17, "1234567890");
  }

  private static UserWithAddressAndPhoneEntity makeComplexUserEntity() {
    return new UserWithAddressAndPhoneEntity(
        "complex_name",
        100,
        "complex user",
        makeAddress(),
        Lists.newArrayList(makePhone(), makePhone()));
  }

  private void dropTablesSilent() {
    try {
      dorm.dropTable(UserEntity.class);
      dorm.dropTable(UserWithAddressAndPhoneEntity.class);
    } catch (Exception ignored) {
    }
  }

  private static HikariDataSource sHikariDataSource;

  @BeforeClass
  public static void beforeClass() {
    sHikariDataSource = Connector.createHikariDataSource();
  }

  @AfterClass
  public static void afterClass() {
    if (sHikariDataSource != null) {
      sHikariDataSource.close();
      sHikariDataSource = null;
    }
  }
}