package ru.otus.kunin.memory;

import java.lang.reflect.Array;
import java.util.function.Supplier;

public class SizeOf {

  public static <T> long measure(Supplier<T> objectCreator) {
    final T obj = objectCreator.get();
    return measure(objectCreator, createTag(obj));
  }

  public static <T> long measure(Supplier<T> objectCreator, String tag) {
    final long almostTakesThatMuchMemory = getAlmostTakesThatMuchMemory(objectCreator, tag);
    // Calling GC outside of a method seems to improve accuracy
    // maybe it is due scope changing
    SynchronousGC.collect();
    return almostTakesThatMuchMemory;
  }

  private static <T> String createTag(T obj) {
    final String stringValue;
    if (obj.getClass().isArray()) {
      final int length = Array.getLength(obj);
      final StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < length; i++) {
        sb.append(Array.get(obj, i));
        if (i != length - 1) {
          sb.append(",");
        }
      }
      sb.append("]");
      stringValue = sb.toString();
    } else {
      stringValue = obj.toString();
    }
    return obj.getClass().getSimpleName() + " " + stringValue;
  }


  private static <T> long getAlmostTakesThatMuchMemory(final Supplier<T> objectCreator, final String tag) {
    final Runtime runtime = Runtime.getRuntime();
    // This looks like magical number for sure.
    // Lowering it makes measurements not robust at best.
    final int size = 1024 * 1024 * 4;
    final Object[] objects = new Object[size];
    SynchronousGC.collect();
    final long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
    for (int i = 0; i < objects.length; i++) {
      objects[i] = objectCreator.get();
    }
    SynchronousGC.collect();
    final long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
    final long almostTakesThatMuchMemory = Math.round((memoryAfter - memoryBefore) / (double) objects.length);
    System.out.printf("[%d] is SizeOf %s \n", almostTakesThatMuchMemory, tag);
    return almostTakesThatMuchMemory;
  }

}
