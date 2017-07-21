package ru.otus.kunin.pararsort;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.kunin.pararsort.algo.Merge;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

public class Parasort {

  public interface Sort<T extends Comparable<? super T>> {
    List<T> sort(List<T> list);
  }

  private static final Logger LOG = LogManager.getLogger(Parasort.class);

  private static final ExecutorService SORT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

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

  public static <T extends Comparable<? super T>> Sort<T> customSort() {
    return list -> {
      LOG.info("List size: " + list.size());
      final int numCores = Runtime.getRuntime().availableProcessors();
      LOG.info("Cores: " + numCores);

      // For small lists just sort in single thread
      if (numCores * 1024 > list.size()) {
        LOG.info("Falling back to system sort");
        return Parasort.<T>system().sort(list);
      }

      final double perCore = .5;
      final int numberOfThreads = Math.max((int) (numCores * perCore), 4);
      final int chunkSize = list.size() / numberOfThreads;
      LOG.info("Chunk size: " + chunkSize);

      final ArrayList<Future<List<T>>> futures = Lists.newArrayList();
      for (int i = 0; i < list.size(); i += chunkSize) {
        final SortTask<T> sortTask = new SortTask(list.subList(i, i + chunkSize));
        futures.add(SORT_EXECUTOR_SERVICE.submit(sortTask));
      }
      LOG.info("Number of tasks/treads running {}", futures.size());

      final List<T> sortedList = futures.stream()
          .map(Futures::getUnchecked)
          .reduce(Merge::mergeList)
          .get();

      return sortedList;
    };
  }

  private static class SortTask<T extends Comparable<? super T>> implements Callable<List<T>> {
    final List<T> toSort;

    public SortTask(final List<T> toSort) {
      this.toSort = toSort;
    }

    @Override
    public List<T> call() throws Exception {
      Collections.sort(toSort);
      return toSort;
    }
  }

}
