package ru.otus.kunin.dorm.base;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.dicache.base.DiCache;
import ru.otus.kunin.dorm.api.Dorm;
import ru.otus.kunin.dorm.api.DormEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachingDorm implements Dorm {

  private final static Logger LOG = LoggerFactory.getLogger(CachingDorm.class.getSimpleName());

  @AutoValue
  public static abstract class CacheKey {

    public abstract Class<?> getType();

    public abstract Long getId();

    public static CacheKey create(Class<?> type, Long id) {
      return new ru.otus.kunin.dorm.base.AutoValue_CachingDorm_CacheKey(type, id);
    }

  }

  private final DiCache<CacheKey, Object> cache;
  private final Dorm impl;
  private final ExecutorService saveExecutor;

  public CachingDorm(final Dorm impl) {
    this.impl = Preconditions.checkNotNull(impl);
    this.cache = new DiCache<>();
    this.saveExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public <T extends DormEntity> void createTable(final Class<T> type) throws SQLException {
    impl.createTable(type);
  }

  @Override
  public <T extends DormEntity> void dropTable(final Class<T> type) throws SQLException {
    impl.dropTable(type);
  }

  @Override
  public <T extends DormEntity> void save(final T value) throws SQLException {
    if (value.isNew()) {
      impl.save(value);
      LOG.info("saved new value: " + value);
    }
    cache.put(CacheKey.create(value.getClass(), value.getId()), value);
    saveInBackground(value);
  }

  private <T extends DormEntity> void saveInBackground(final T value) {
    saveExecutor.execute(() -> {
      try {
        impl.save(value);
        LOG.info("updated value in background: " + value);
      } catch (SQLException e) {
        LOG.error("Failed to save value " + value, e);
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public <T extends DormEntity> Optional<T> load(final long id, final Class<T> clazz) throws SQLException {
    final CacheKey cacheKey = CacheKey.create(clazz, id);
    final T value = (T) cache.get(cacheKey);
    if (null != value) {
      LOG.info("read from cache: " + value);
      return Optional.of(value);
    }
    final Optional<T> loaded = impl.load(id, clazz);
    loaded.ifPresent(v -> cache.put(cacheKey, v));
    loaded.ifPresent(v-> LOG.info("read from source: " + value));
    return loaded;
  }

  @Override
  public <T extends DormEntity> List<T> loadAll(final Class<T> clazz) throws SQLException {
    final List<T> values = impl.loadAll(clazz);
    for (final T value : values) {
      cache.put(CacheKey.create(clazz, value.getId()), value);
    }
    return values;
  }

  @Override
  public void close() throws Exception {
    saveExecutor.shutdownNow().forEach(Runnable::run);
    impl.close();
    cache.close();
  }
}
