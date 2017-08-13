package ru.otus.kunin.dicache.event;

import com.google.common.collect.Lists;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import java.util.concurrent.CopyOnWriteArraySet;

public class CompositeEventListenerImpl<K, V> implements CompositeEventListener<K, V> {

  private final CopyOnWriteArraySet<CacheListenerAdapter<K, V>> listeners = new CopyOnWriteArraySet<>();

  @Override
  public void addListener(final CacheListenerAdapter<K, V> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(final CacheListenerAdapter<K, V> listener) {
    listeners.remove(listener);
  }

  @Override
  public void onCreated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (final CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      listeners.stream()
          .filter(l -> l.getCreatedListener().isPresent())
          .filter(l -> l.getEntryEventFilter().map(filter -> filter.evaluate(event)).orElse(true))
          .forEach(l -> l.getCreatedListener().get().onCreated(Lists.newArrayList(event)));
    }
  }

  @Override
  public void onExpired(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (final CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      listeners.stream()
          .filter(l -> l.getExpiredListener().isPresent())
          .filter(l -> l.getEntryEventFilter().map(filter -> filter.evaluate(event)).orElse(true))
          .forEach(l -> l.getExpiredListener().get().onExpired(Lists.newArrayList(event)));
    }
  }

  @Override
  public void onRemoved(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (final CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      listeners.stream()
          .filter(l -> l.getRemovedListener().isPresent())
          .filter(l -> l.getEntryEventFilter().map(filter -> filter.evaluate(event)).orElse(true))
          .forEach(l -> l.getRemovedListener().get().onRemoved(Lists.newArrayList(event)));
    }
  }

  @Override
  public void onUpdated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (final CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      listeners.stream()
          .filter(l -> l.getUpdatedListener().isPresent())
          .filter(l -> l.getEntryEventFilter().map(filter -> filter.evaluate(event)).orElse(true))
          .forEach(l -> l.getUpdatedListener().get().onUpdated(Lists.newArrayList(event)));
    }
  }
}
