package ru.otus.kunin.dcache.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import ru.otus.kunin.dcache.Dcache;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toMap;

public class DcacheImpl<K, V> implements Dcache<K, V> {

  private final ConcurrentMap<K, DcacheEntry<K, V>> map = Maps.newConcurrentMap();
  private final Optional<CacheLoader<K, V>> cacheLoader;
  private final Optional<CacheLoader<K, V>> cacheWriter;

  public DcacheImpl() {
    this(Optional.empty(), Optional.empty());
  }

  public DcacheImpl(final Optional<CacheLoader<K, V>> cacheLoader,
                    final Optional<CacheLoader<K, V>> cacheWriter) {
    this.cacheLoader = cacheLoader;
    this.cacheWriter = cacheWriter;
  }

  @Override
  public V get(final K key) {
    final Optional<DcacheEntry<K, V>> entry = Optional.ofNullable(map.get(validateKey(key)));
    // TODO load if not present
    return entry.map(DcacheEntry::getValue).orElse(null);
  }

  @Override
  public Map<K, V> getAll(final Set<? extends K> set) {
    return set.stream()
        .map(map::get)
        .filter(ref -> ref != null && ref.get() != null)
        .collect(
            toMap(DcacheEntry::getKey,
                DcacheEntry::getValue,
                MoreObjects::firstNonNull)); // reduce keys
  }

  @Override
  public boolean containsKey(final K key) {
    return Optional.ofNullable(map.get(validateKey(key))).map(SoftReference::get).isPresent();
  }

  @Override
  public void loadAll(final Set<? extends K> keys,
                      final boolean reload,
                      final CompletionListener completionListener) {
    // TODO load all
    // TODO notify
  }

  @Override
  public void put(final K k, final V v) {
    map.put(k, createEntry(k, v));
    // TODO notify
  }

  @Override
  public V getAndPut(final K k, final V v) {
    final Optional<DcacheEntry<K, V>> oldEntry = Optional.of(map.replace(k, createEntry(k, v)));
    // TODO notify
    return oldEntry.map(DcacheEntry::getValue).orElse(null);
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> map) {
    map.forEach(this::put);
  }

  @Override
  public boolean putIfAbsent(final K k, final V v) {
    final DcacheEntry<K, V> oldEntry = map.putIfAbsent(k, createEntry(k, v));
    return Optional.ofNullable(oldEntry).map(DcacheEntry::getValue).isPresent();
    // TODO notify
  }

  @Override
  public boolean remove(final K k) {
    return Optional.ofNullable(map.remove(k)).map(DcacheEntry::getValue).isPresent();
  }

  @Override
  public boolean remove(final K k, final V v) {
    return map.remove(k, createEntry(k, v));
  }

  @Override
  public V getAndRemove(final K k) {
    return Optional.ofNullable(map.remove(k)).map(DcacheEntry::getValue).orElse(null);
    // TODO notify
  }

  @Override
  public boolean replace(final K k, final V oldValue, final V newValue) {
    return map.replace(k, createEntry(k, oldValue), createEntry(k, newValue));
    // TODO notify
  }

  @Override
  public boolean replace(final K k, final V v) {
    return false;
    // TODO notify
  }

  @Override
  public V getAndReplace(final K k, final V v) {
    return null;
    // TODO notify
  }

  @Override
  public void removeAll(final Set<? extends K> set) {
    // TODO notify
  }

  @Override
  public void removeAll() {
    // TODO notify
  }

  @Override
  public void clear() {
  }

  @Override
  public <C extends Configuration<K, V>> C getConfiguration(final Class<C> aClass) {
    return null;
  }

  @Override
  public <T> T invoke(final K k,
                      final EntryProcessor<K, V, T> entryProcessor,
                      final Object... objects) throws EntryProcessorException {
    return null;
  }

  @Override
  public <T> Map<K, EntryProcessorResult<T>> invokeAll(final Set<? extends K> set,
                                                       final EntryProcessor<K, V, T> entryProcessor,
                                                       final Object... objects) {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public CacheManager getCacheManager() {
    return null;
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public <T> T unwrap(final Class<T> aClass) {
    // TODO return self if the same class?
    return null;
  }

  @Override
  public void registerCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

  }

  @Override
  public void deregisterCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return null;
  }

  private K validateKey(K key) {
    return Preconditions.checkNotNull(key, "key must be not null");
  }

  private V validateValue(V value) {
    return Preconditions.checkNotNull(value, "value must be not null");
  }

  private DcacheEntry<K, V> createEntry(final K k, final V v) {
    return DcacheEntry.create(validateKey(k), validateValue(v));
  }

}
