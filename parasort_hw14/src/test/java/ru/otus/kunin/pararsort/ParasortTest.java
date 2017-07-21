package ru.otus.kunin.pararsort;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ParasortTest {

  @Parameters(name = "list size = {0}")
  public static List<Integer> sizes() {
    return Lists.newArrayList(
        2 << 15,
        2 << 18,
        2 << 20,
        2 << 22);
  }

  @Parameter
  public int size;
  List<Integer> list;

  @Before
  public void setUp() throws Exception {
    Random random = new Random(100);
    list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
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

  @Test
  public void testCustomSortWithExecutor() throws Exception {
    Parasort.Sort<Integer> sortWithExecutor = Parasort.customSortWithExecutor();
    List<Integer> sorted = sortWithExecutor.sort(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testCustomSortWithNewThread() throws Exception {
    Parasort.Sort<Integer> sortWithNewThreads = Parasort.customSortWithNewThreads();
    List<Integer> sorted = sortWithNewThreads.sort(list);
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