package ru.otus.kunin.pararsort.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * Some merge code from the Internet.
 */
public class Merge {

  public static <T extends Comparable<? super T>> List<T> mergeList(List<T> a, List<T> b) {
    final List<T> returnList = new ArrayList<>(a.size() + b.size());
    int indexA = 0;
    int indexB = 0;
    while (indexA < a.size() && indexB < b.size()) {
      if (a.get(indexA).compareTo(b.get(indexB)) < 0) {
        returnList.add(a.get(indexA));
        indexA++;
      } else {
        returnList.add(b.get(indexB));
        indexB++;
      }
    }
    // Append the remaining array
    if (indexA < a.size()) {
      returnList.addAll(a.subList(indexA, a.size()));
    }
    if (indexB < b.size()) {
      returnList.addAll(b.subList(indexB, b.size()));
    }
    return returnList;
  }
}
