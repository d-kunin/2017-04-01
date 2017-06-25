package ru.otus.kunin.dorm.hibernate;

import com.google.common.collect.ImmutableMap;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.api.DormEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class DormHibernateImpl implements Dorm {

  private final SessionFactory sessionFactory;

  public DormHibernateImpl(Set<Class<? extends DormEntity>> annotatedClasses) {
    StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
    final ImmutableMap<String, Object> settings = ImmutableMap.<String, Object>builder()
        .put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect")
        .put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver")
        .put(Environment.URL, "jdbc:mysql://localhost:3306/db_example")
        .put(Environment.USER, "tully")
        .put(Environment.PASS, "tully")
        .put(Environment.HBM2DDL_AUTO, "create")
//        .put(Environment.SHOW_SQL, true)
        .build();
    registryBuilder.applySettings(settings);
    final StandardServiceRegistry registry = registryBuilder.build();

    final MetadataSources metadataSources = new MetadataSources(registry);
    annotatedClasses.forEach(metadataSources::addAnnotatedClass);

    sessionFactory = metadataSources.getMetadataBuilder()
        .build().getSessionFactoryBuilder().build();
  }

  @Override
  public <T extends DormEntity> void createTable(Class<T> type) throws SQLException {
    // noop
  }

  @Override
  public <T extends DormEntity> void dropTable(Class<T> type) throws SQLException {
    // noop
  }

  @Override
  public <T extends DormEntity> void save(T value) throws SQLException {
    withSession(session -> session.save(value));
  }

  @Override
  public <T extends DormEntity> Optional<T> load(long id, Class<T> clazz) throws SQLException {
    return withSession(session -> {
      final Query query = session.createQuery("from " + clazz.getSimpleName() + " where id = :id");
      query.setParameter("id", id);
      return Optional.ofNullable((T)query.uniqueResult());
    });
  }

  @Override
  public <T extends DormEntity> List<T> loadAll(Class<T> clazz) throws SQLException {
    return withSession(session -> session.createQuery("from " +  clazz.getSimpleName()).list());
  }

  private <R> R withSession(Function<Session, R> function) {
    try (Session session = sessionFactory.openSession()) {
      final Transaction transaction = session.beginTransaction();
      final R result = function.apply(session);
      transaction.commit();
      return result;
    }
  }

  @Override
  public void close() throws Exception {
    sessionFactory.close();
  }
}
