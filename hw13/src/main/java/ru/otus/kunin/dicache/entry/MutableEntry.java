package ru.otus.kunin.dicache.entry;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class MutableEntry<K, V> implements javax.cache.processor.MutableEntry<K, V> {

  /*
    getValue() should generate hit/miss
   */

  private final K key;
  private final V oldValue;

  private boolean removed = false;
  private V newValue = null;

  public MutableEntry(final K key, final V oldValue) {
    this.key = key;
    this.oldValue = oldValue;
  }

  public boolean isRemoved() {
    return removed;
  }

  public boolean isNewValueSet() {
    return null != newValue;
  }

  public V getNewValue() {
    return newValue;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    if (!exists()) return null;
    return MoreObjects.firstNonNull(newValue, oldValue);
  }

  @Override
  public <T> T unwrap(final Class<T> clazz) {
    if (this.getClass().isAssignableFrom(clazz)) {
      return (T) this;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public boolean exists() {
    if (removed) return false;
    return oldValue != null || newValue != null;
  }

  @Override
  public void remove() {
    removed = true;
    newValue = null;
  }

  @Override
  public void setValue(final V value) {
    Preconditions.checkNotNull(value);
    removed = false;
    newValue = value;
  }
}
