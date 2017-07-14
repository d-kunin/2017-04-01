package ru.otus.kunin.dicache.base.event;

import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

public interface CompositeEventListener<K, V>
    extends
      CacheEntryCreatedListener<K, V>,
      CacheEntryUpdatedListener<K, V>,
      CacheEntryRemovedListener<K, V>,
      CacheEntryExpiredListener<K, V>
{

  void addListener(CacheListenerAdapter<K, V> listener);

  void removeListener(CacheListenerAdapter<K, V> listener);
}
