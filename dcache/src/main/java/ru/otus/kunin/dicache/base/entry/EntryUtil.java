package ru.otus.kunin.dicache.base.entry;

import javax.cache.Cache;

public class EntryUtil {

  private EntryUtil() {}

  public static <K, V> StrongEntry<K, V> strongify(Cache.Entry<K, V> entry) {
    return StrongEntry.create(entry.getKey(), entry.getValue());
  }

  public static <K, V> MutableEntry<K, V> mutable(Cache.Entry<K, V> entry) {
    return new MutableEntry<>(entry.getKey(), entry.getValue());
  }

}
