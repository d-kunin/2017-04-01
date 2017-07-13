package ru.otus.kunin.dcache.impl;

import com.google.common.base.Preconditions;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.*;
import java.util.Objects;
import java.util.Optional;

public class CacheListenerAdapter<K, V> {

  public static <K, V> CacheListenerAdapter<K, V> fromOnCreatedListener(CacheEntryCreatedListener<K ,V> createdListener) {
    Preconditions.checkNotNull(createdListener);
    final MutableCacheEntryListenerConfiguration<K, V> configuration = new MutableCacheEntryListenerConfiguration<>(
        () -> createdListener, () -> null, false, false
    );
    return fromConfiguration(configuration);
  }

  public static <K, V> CacheListenerAdapter<K, V> fromOnUpdatedListener(CacheEntryUpdatedListener<K ,V> updatedListener) {
    Preconditions.checkNotNull(updatedListener);
    final MutableCacheEntryListenerConfiguration<K, V> configuration = new MutableCacheEntryListenerConfiguration<>(
        () -> updatedListener, () -> null, false, false
    );
    return fromConfiguration(configuration);
  }

  public static <K, V> CacheListenerAdapter<K, V> fromOnRemovedListener(CacheEntryRemovedListener<K ,V> removedListener) {
    Preconditions.checkNotNull(removedListener);
    final MutableCacheEntryListenerConfiguration<K, V> configuration = new MutableCacheEntryListenerConfiguration<>(
        () -> removedListener, () -> null, false, false
    );
    return fromConfiguration(configuration);
  }

  public static <K, V> CacheListenerAdapter<K, V> fromOnExpiredListener(CacheEntryExpiredListener<K ,V> expiredListener) {
    Preconditions.checkNotNull(expiredListener);
    final MutableCacheEntryListenerConfiguration<K, V> configuration = new MutableCacheEntryListenerConfiguration<>(
        () -> expiredListener, () -> null, false, false
    );
    return fromConfiguration(configuration);
  }

  public static <K, V> CacheListenerAdapter<K, V> fromConfiguration(final CacheEntryListenerConfiguration<K, V> configuration) {
    Preconditions.checkNotNull(configuration);

    final Optional<CacheEntryEventFilter<? super K, ? super V>> eventFilter =
        Optional.ofNullable(configuration.getCacheEntryEventFilterFactory()).map(Factory::create);

    final CacheEntryListener<? super K, ? super V> entryListener =
        Preconditions.checkNotNull(configuration.getCacheEntryListenerFactory().create());

    final Optional<CacheEntryCreatedListener<K, V>> createdListener = entryListener instanceof CacheEntryCreatedListener
        ? Optional.of((CacheEntryCreatedListener<K, V>) entryListener) : Optional.empty();
    final Optional<CacheEntryUpdatedListener<K, V>> updatedListener = entryListener instanceof CacheEntryUpdatedListener
        ? Optional.of((CacheEntryUpdatedListener<K, V>) entryListener) : Optional.empty();
    final Optional<CacheEntryRemovedListener<K, V>> removedListener = entryListener instanceof CacheEntryRemovedListener
        ? Optional.of((CacheEntryRemovedListener<K, V>) entryListener) : Optional.empty();
    final Optional<CacheEntryExpiredListener<K, V>> expiredListener = entryListener instanceof CacheEntryExpiredListener
        ? Optional.of((CacheEntryExpiredListener<K, V>) entryListener) : Optional.empty();

    if (!createdListener.isPresent()
        && !updatedListener.isPresent()
        && !removedListener.isPresent()
        && !expiredListener.isPresent()) {
      throw new IllegalArgumentException("CacheEntryListener must implement one of its subinterfaces.");
    }
    return new CacheListenerAdapter<>(
        configuration,
        createdListener,
        updatedListener,
        removedListener,
        expiredListener,
        eventFilter);
  }

  private final CacheEntryListenerConfiguration<K, V> configuration;
  private final Optional<CacheEntryCreatedListener<K, V>> createdListener;
  private final Optional<CacheEntryUpdatedListener<K, V>> updatedListener;
  private final Optional<CacheEntryRemovedListener<K, V>> removedListener;
  private final Optional<CacheEntryExpiredListener<K, V>> expiredListener;
  private final Optional<CacheEntryEventFilter<? super K, ? super V>> entryEventFilter;

  private CacheListenerAdapter(final CacheEntryListenerConfiguration<K, V> configuration,
                               final Optional<CacheEntryCreatedListener<K, V>> createdListener,
                               final Optional<CacheEntryUpdatedListener<K, V>> updatedListener,
                               final Optional<CacheEntryRemovedListener<K, V>> removedListener,
                               final Optional<CacheEntryExpiredListener<K, V>> expiredListener,
                               final Optional<CacheEntryEventFilter<? super K, ? super V>> entryEventFilter) {
    this.configuration = Preconditions.checkNotNull(configuration);
    this.createdListener = Preconditions.checkNotNull(createdListener);
    this.updatedListener = Preconditions.checkNotNull(updatedListener);
    this.removedListener = Preconditions.checkNotNull(removedListener);
    this.expiredListener = Preconditions.checkNotNull(expiredListener);
    this.entryEventFilter = Preconditions.checkNotNull(entryEventFilter);
  }

  public CacheEntryListenerConfiguration<K, V> getConfiguration() {
    return configuration;
  }

  public Optional<CacheEntryCreatedListener<K, V>> getCreatedListener() {
    return createdListener;
  }

  public Optional<CacheEntryUpdatedListener<K, V>> getUpdatedListener() {
    return updatedListener;
  }

  public Optional<CacheEntryRemovedListener<K, V>> getRemovedListener() {
    return removedListener;
  }

  public Optional<CacheEntryExpiredListener<K, V>> getExpiredListener() {
    return expiredListener;
  }

  public Optional<CacheEntryEventFilter<? super K, ? super V>> getEntryEventFilter() {
    return entryEventFilter;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CacheListenerAdapter<?, ?> that = (CacheListenerAdapter<?, ?>) o;
    return Objects.equals(configuration, that.configuration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(configuration);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + configuration.toString() + "]";
  }
}
