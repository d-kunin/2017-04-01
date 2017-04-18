package ru.otus.kunin.memory;

public class Main {

  public static void main(String[] args) {
    System.out.println("Memory adventures");

    SizeOf.measure(() -> new boolean[7]);
    SynchronousGC.collect();

    SizeOf.measure(() -> new boolean[8]);
    SynchronousGC.collect();

    SizeOf.measure(() -> new boolean[9]);
    SynchronousGC.collect();

    SizeOf.measure(Object::new);
    SynchronousGC.collect();

    SizeOf.measure(String::new);
    SynchronousGC.collect();

    SizeOf.measure(() -> new String(new char[0]));
    SynchronousGC.collect();
  }

}
