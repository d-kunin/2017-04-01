package ru.otus.kunin.dcache.base.event;

import com.google.common.base.Preconditions;

import javax.cache.Cache;
import javax.cache.event.EventType;
import java.util.Objects;

public class CacheEntryEvent<K, V> extends javax.cache.event.CacheEntryEvent<K, V> {

  private final K key;
  private final V value;
  private final V oldValue;

  public CacheEntryEvent(final Cache source,
                         final EventType eventType,
                         final K key,
                         final V value,
                         final V oldValue) {
    super(Preconditions.checkNotNull(source), Preconditions.checkNotNull(eventType));
    this.key = Preconditions.checkNotNull(key);
    this.value = value;
    this.oldValue = oldValue;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V getOldValue() {
    return oldValue;
  }

  @Override
  public boolean isOldValueAvailable() {
    return null == getOldValue();
  }

  @Override
  public <T> T unwrap(final Class<T> clazz) {
    if (this.getClass().isAssignableFrom(clazz)) {
      return (T) this;
    }
    throw new IllegalArgumentException("Is not assignable from " + clazz);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CacheEntryEvent<?, ?> that = (CacheEntryEvent<?, ?>) o;
    return Objects.equals(getKey(), that.getKey()) &&
        Objects.equals(getValue(), that.getValue()) &&
        Objects.equals(getOldValue(), that.getOldValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKey(), getValue(), getOldValue());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CacheEntryEvent{");
    sb.append("type=").append(getEventType());
    sb.append(", key=").append(key);
    sb.append(", value=").append(value);
    sb.append(", oldValue=").append(oldValue);
    sb.append('}');
    return sb.toString();
  }
}
