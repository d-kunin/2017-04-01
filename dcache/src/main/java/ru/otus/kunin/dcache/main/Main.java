package ru.otus.kunin.dcache.main;

import com.google.common.collect.ImmutableSet;
import ru.otus.kunin.dcache.impl.DcacheImpl;

public class Main {

  public static void main(String[] args) {
    final DcacheImpl<String, String> cache = new DcacheImpl<>();
    cache.putIfAbsent("key1", "v1");
    cache.putIfAbsent("key1", "v1_fucked");
    cache.putIfAbsent("key2", "v2");
    cache.putIfAbsent("key3", "v3");
    cache.remove("key3");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
    cache.remove("key1", "v1");
    cache.remove("key2", "v1");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
    cache.replace("key2", "v2", "v2_updated");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
  }

}
