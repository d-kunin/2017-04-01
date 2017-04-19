package ru.otus.kunin.memory;

import java.util.Arrays;
import java.util.function.Supplier;

public class SizeOf {

  public static <T> long measure(Supplier<T> objectCreator) {
    final T obj = objectCreator.get();
    final String stringValue;
    // Not all types are here, but the ones we need
    if (obj instanceof Object[]) {
      stringValue = Arrays.toString((Object[]) obj);
    } else if (obj instanceof int[]) {
      stringValue = Arrays.toString((int[]) obj);
    } else if (obj instanceof boolean[]) {
      stringValue = Arrays.toString((boolean[]) obj);
    } else {
      stringValue = obj.toString();
    }
    return measure(objectCreator, obj.getClass().getSimpleName() + " " + stringValue);
  }


  public static <T> long measure(Supplier<T> objectCreator, String tag) {
    final long almostTakesThatMuchMemory = getAlmostTakesThatMuchMemory(objectCreator, tag);
    // Calling GC outside of a method seems to improve accuracy
    // maybe it is due scope changing
    SynchronousGC.collect();
    return almostTakesThatMuchMemory;
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
