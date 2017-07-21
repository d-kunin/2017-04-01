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
        65_536,
        524_288,
        2_097_152,
        8_388_608);
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
    List<Integer> sorted = Parasort.system(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testStreamSort() throws Exception {
    List<Integer> sorted = Parasort.stream(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testArraysParallelSort() throws Exception {
    List<Integer> sorted = Parasort.arraysParallel(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testCustomSortWithExecutor() throws Exception {
    List<Integer> sorted = Parasort.customSortWithExecutor(list);
    assertTrue(isSorted(sorted));
  }

  @Test
  public void testCustomSortWithNewThread() throws Exception {
    List<Integer> sorted = Parasort.customSortWithNewThreads(list);
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