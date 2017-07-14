package ru.otus.kunin.dicache.base.event;

import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

public interface SimpleCacheEventListener<K, V> extends
    CacheEntryCreatedListener<K, V>,
    CacheEntryUpdatedListener<K, V>,
    CacheEntryRemovedListener<K, V>,
    CacheEntryExpiredListener<K, V> {

  @Override
  default void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
  }

  @Override
  default void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
  }

  @Override
  default void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
  }

  @Override
  default void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
  }
}
