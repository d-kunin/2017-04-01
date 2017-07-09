package ru.otus.kunin.dcache.impl;


import com.google.common.base.Preconditions;

import javax.cache.Cache;
import java.lang.ref.SoftReference;
import java.util.Objects;

public class DcacheEntry<K, V> extends SoftReference<V> implements Cache.Entry<K, V> {

  private final K key;

  public DcacheEntry(final K key, final V value) {
    super(value);
    this.key = key;
  }

  public static <K, V> DcacheEntry<K, V> create(K key, V value) {
    return new DcacheEntry<>(Preconditions.checkNotNull(key),
                             Preconditions.checkNotNull(value));
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return get();
  }

  @Override
  public <T> T unwrap(final Class<T> clazz) {
    if (clazz == DcacheEntry.class) {
      return (T) this;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("{");
    sb.append("k=").append(key);
    sb.append("v=").append(getValue());
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final DcacheEntry<?, ?> that = (DcacheEntry<?, ?>) o;
    return Objects.equals(key, that.key) &&
           Objects.equals(getValue(), that.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, getValue());
  }

  public boolean hasValue() {
    return getValue() != null;
  }
}
