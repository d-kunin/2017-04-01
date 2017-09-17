package net.kundzi.dicache.entry;

import com.google.common.base.Preconditions;

import javax.cache.Cache;
import java.lang.ref.SoftReference;
import java.util.Objects;

public class SoftEntry<K, V> extends SoftReference<V> implements Cache.Entry<K, V> {

  private final K key;

  public SoftEntry(final K key, final V value) {
    super(value);
    this.key = key;
  }

  public static <K, V> SoftEntry<K, V> create(K key, V value) {
    return new SoftEntry<>(Preconditions.checkNotNull(key),
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
    if (clazz == SoftEntry.class) {
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
    final SoftEntry<?, ?> that = (SoftEntry<?, ?>) o;
    return Objects.equals(key, that.key) &&
        Objects.equals(getValue(), that.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, getValue());
  }

  public boolean isGarbageCollected() {
    return null == getValue();
  }
}
