package ru.otus.kunin.pararsort;

import com.google.common.collect.Lists;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Parasort {

  interface Sort<T extends Comparable<? super T>> {

    List<T> sort(List<T> list);
  }

  public static <T extends Comparable<? super T>> Sort<T> system() {
    return list -> {
      ArrayList<T> result = Lists.newArrayList(list);
      Collections.sort(result);
      return result;
    };
  }

  public static <T extends Comparable<? super T>> Sort<T> stream() {
    return list ->
        list.parallelStream()
            .sorted()
            .collect(toList());
  }

  public static <T extends Comparable<? super T>> Sort<T> arraysParallel() {
    return list -> {
      if (list.isEmpty()) {
        return list;
      }
      T[] asArray = list.toArray((T[]) Array.newInstance(list.get(0).getClass(), list.size()));
      Arrays.parallelSort(asArray);
      return Lists.newArrayList(asArray);
    };
  }

}
