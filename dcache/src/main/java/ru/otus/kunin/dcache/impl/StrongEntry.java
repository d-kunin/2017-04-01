package ru.otus.kunin.dcache.impl;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import javax.cache.Cache;

@AutoValue
public abstract class StrongEntry<K, V> implements Cache.Entry<K, V> {

  public static <K, V> StrongEntry<K, V> create(K key, V value) {
    return new ru.otus.kunin.dcache.impl.AutoValue_StrongEntry<>(key, value);
  }

  @Override
  @Nullable
  public abstract V getValue();

  public boolean hasValue() {
    return null != getValue();
  }

  @Override
  public <T> T unwrap(final Class<T> clazz) {
    if (getClass().isAssignableFrom(clazz)) {
      return (T) this;
    }
    throw new IllegalArgumentException("Not assignable from " + clazz);
  }

}
