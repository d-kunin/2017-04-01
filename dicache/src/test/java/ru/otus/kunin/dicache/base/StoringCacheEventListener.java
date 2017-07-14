package ru.otus.kunin.dicache.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ru.otus.kunin.dicache.base.event.SimpleCacheEventListener;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import java.util.List;

public class StoringCacheEventListener<K, V> implements SimpleCacheEventListener<K, V> {

  List<CacheEntryEvent<? extends K, ? extends V>> events = Lists.newArrayList();

  public List<CacheEntryEvent<? extends K, ? extends V>> getEvents() {
    return ImmutableList.copyOf(events);
  }

  @Override
  public void onCreated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    cacheEntryEvents.forEach(events::add);
  }

  @Override
  public void onExpired(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    cacheEntryEvents.forEach(events::add);
  }

  @Override
  public void onRemoved(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    cacheEntryEvents.forEach(events::add);
  }

  @Override
  public void onUpdated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    cacheEntryEvents.forEach(events::add);
  }
}
