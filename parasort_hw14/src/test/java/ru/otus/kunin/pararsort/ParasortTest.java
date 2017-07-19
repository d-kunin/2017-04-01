package ru.otus.kunin.pararsort;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class ParasortTest {

  List<Integer> list;

  private static final int SIZE = 1000000;

  @Before
  public void setUp() throws Exception {
    Random random = new Random(100);
    list = new ArrayList<>(SIZE);
    for (int i = 0; i < SIZE; i++) {
      list.add(random.nextInt());
    }
    assertTrue(isUnsortedSorted(list));
  }

  @Test
  public void testSystemSort() throws Exception {
    Parasort.Sort<Integer> system = Parasort.system();
    List<Integer> sorted = system.sort(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testStreamSort() throws Exception {
    Parasort.Sort<Integer> stream = Parasort.stream();
    List<Integer> sorted = stream.sort(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testArraysParallelSort() throws Exception {
    Parasort.Sort<Integer> arraysParallel = Parasort.arraysParallel();
    List<Integer> sorted = arraysParallel.sort(list);
    assertTrue(isSorted(sorted));
  }
  
  <T extends Comparable<? super T>> boolean isSorted(List<T> list) {
    for (int i = 0; i < list.size() - 1; i++) {
      if (list.get(i + 1).compareTo(list.get(i)) < 0) {
        return false;
      }
    }
    return true;
  }

  <T extends Comparable<? super T>> boolean isUnsortedSorted(List<T> list) {
    return !isSorted(list);
  }

}