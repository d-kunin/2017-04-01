package ru.otus.kunin.dcache.main;

import com.google.common.collect.ImmutableSet;
import ru.otus.kunin.dcache.impl.DcacheImpl;

public class Main {

  public static void main(String[] args) {
    final DcacheImpl<String, String> cache = new DcacheImpl<>();
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2")));
  }

}
