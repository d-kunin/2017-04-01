package ru.otus.kunin.dcache.impl;

import javax.cache.Cache;

public class RefUtil {

  private RefUtil() {}

  public static <K, V> StrongEntry<K, V> strongify(Cache.Entry<K, V> entry) {
    return new ru.otus.kunin.dcache.impl.AutoValue_StrongEntry<>(entry.getKey(), entry.getValue());
  }

  public static <K, V> MutableEntry<K, V> mutable(Cache.Entry<K, V> entry) {
    return new MutableEntry<>(entry.getKey(), entry.getValue());
  }

}
