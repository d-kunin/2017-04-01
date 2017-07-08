package ru.otus.kunin.dcache.impl;

import com.google.auto.value.AutoValue;

import javax.cache.Cache;

@AutoValue
public abstract class DcacheEntry<K, V> implements Cache.Entry<K, V> {

  public static <K, V> DcacheEntry<K, V> create(K key, V value) {
    return new ru.otus.kunin.dcache.impl.AutoValue_DcacheEntry<>(key, value);
  }

  @Override
  public <T> T unwrap(final Class<T> clazz) {
    throw new UnsupportedOperationException("Not supported " + clazz);
  }
}
