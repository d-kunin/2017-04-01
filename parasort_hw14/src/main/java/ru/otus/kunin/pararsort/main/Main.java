package ru.otus.kunin.pararsort.main;

import com.google.common.collect.Lists;
import ru.otus.kunin.pararsort.Parasort;

import java.util.ArrayList;

public class Main {

  public static void main(String[] args) {
    final ArrayList<Integer> list = Lists.newArrayList(1, 2, 3);
    Parasort.<Integer>customSort().sort(list);
  }

}
