package ru.otus.kunin.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

  public static void main(String[] args) {
    System.out.println("Memory adventures");

    // Boolean does take 1 bit in array. Wow.
    SizeOf.measure(() -> new boolean[7]);
    SizeOf.measure(() -> new boolean[8]);
    SizeOf.measure(() -> new boolean[9]);

    // Otus requirements
    SizeOf.measure(Object::new);
    SizeOf.measure(String::new);
    SizeOf.measure(() -> new String(new char[0]));

    SizeOf.measure(() -> new ArrayList());
    SizeOf.measure(() -> new LinkedList());
    SizeOf.measure(() -> new HashMap());
    SizeOf.measure(() -> new ConcurrentHashMap());
    SizeOf.measure(() -> new LinkedHashSet());
  }

}
