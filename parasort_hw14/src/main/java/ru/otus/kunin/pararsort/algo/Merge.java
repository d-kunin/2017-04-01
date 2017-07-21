package ru.otus.kunin.pararsort.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * Some merge code from the internet.
 */
public class Merge {

  public static <T extends Comparable<? super T>> List<T> mergeList(List<T> a, List<T> b) {
    List<T> returnList = new ArrayList<>(a.size() + b.size());
    List<T> smallerList = a.size() <= b.size() ? a : b;
    List<T> longerList = smallerList.equals(a) ? b : a;
    int s1 = 0;
    int s2 = 0;
    while (s1 < smallerList.size() && s2 < longerList.size()) {
      if (smallerList.get(s1).compareTo(longerList.get(s2)) < 0) {
        returnList.add(smallerList.get(s1));
        s1++;
      } else {
        returnList.add(longerList.get(s2));
        s2++;
      }
    }
    // Append the remaining array
    if (s1 < smallerList.size()) {
      returnList.addAll(smallerList.subList(s1, smallerList.size()));
    }
    if (s2 < longerList.size()) {
      returnList.addAll(longerList.subList(s2, longerList.size()));
    }
    return returnList;
  }
}
