package ru.otus.kunin.dcache.main;

import com.google.common.collect.ImmutableSet;
import ru.otus.kunin.dcache.base.DiCache;
import ru.otus.kunin.dcache.base.event.CacheListenerAdapter;
import ru.otus.kunin.dcache.base.event.CompositeEventListenerImpl;

import javax.cache.processor.EntryProcessor;
import java.util.Optional;

public class Main {

  public static void main(String[] args) throws InterruptedException {

    final CompositeEventListenerImpl<String, String> eventListener = new CompositeEventListenerImpl<>();
    eventListener.addListener(CacheListenerAdapter.fromOnCreatedListener(
        cacheEntryEvents ->
            cacheEntryEvents.forEach(e -> System.out.println("Created: " + e))));
    eventListener.addListener(CacheListenerAdapter.fromOnRemovedListener(
        cacheEntryEvents ->
            cacheEntryEvents.forEach(e -> System.out.println("Removed: " + e))));
    eventListener.addListener(CacheListenerAdapter.fromOnUpdatedListener(
        cacheEntryEvents ->
            cacheEntryEvents.forEach(e -> System.out.println("Updated: " + e))));
    eventListener.addListener(CacheListenerAdapter.fromOnExpiredListener(
        cacheEntryEvents ->
            cacheEntryEvents.forEach(e -> System.out.println("Expired: " + e))));
    
    final DiCache<String, String> cache = new DiCache<>(Optional.of(eventListener));

    cache.putIfAbsent("key1", "v1");
    cache.putIfAbsent("key1", "v1_fucked");
    cache.putIfAbsent("key2", "v2");
    cache.putIfAbsent("key3", "v3");
    cache.remove("key3");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
    cache.remove("key1", "v1");
    cache.remove("key2", "v1");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
    cache.replace("key2", "v2", "v2_updated");
    System.out.println(cache.getAll(ImmutableSet.of("key1", "key2", "key3")));
    cache.put("key4", "v4");
    cache.put("key5", "v5");

    final EntryProcessor<String, String, Integer> testProcessor = (entry, arguments) -> {
      if ("key4".equals(entry.getKey())) {
        entry.setValue("hahahaha");
      }
      if ("key2".equals(entry.getKey())) {
        entry.remove();
      }

      if (entry.exists()) {
        return entry.getValue().length();
      } else {
        return 0;
      }
    };
    final ImmutableSet<String> keys = ImmutableSet.of("key1", "key2", "key3", "key4", "key5");
    System.out.println(cache.getAll(keys));
    cache.invokeAll(keys, testProcessor)
        .forEach((s, result) -> System.out.println(s + " value is as long as " + result.get()));
    System.out.println(cache.getAll(keys));

    // Expire entry and wait to see it cleaned up
    cache.expireEntry("key4");
    Thread.sleep(1000);
    cache.close();
  }

}
