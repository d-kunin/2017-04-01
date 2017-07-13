package ru.otus.kunin.dcache.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ru.otus.kunin.dcache.Dcache;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.event.EventType;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toMap;
import static ru.otus.kunin.dcache.impl.RefUtil.mutable;

public class DcacheImpl<K, V> implements Dcache<K, V> {

  private final ConcurrentMap<K, SoftEntry<K, V>> map = Maps.newConcurrentMap();
  private final AtomicBoolean isClosed = new AtomicBoolean(false);
  private final Optional<CompositeEventListener<K, V>> eventListener;

  public DcacheImpl(final Optional<CompositeEventListener<K, V>> eventListener) {
    this.eventListener = eventListener;
  }

  @Override
  public V get(final K key) {
    throwIfClosed();
    final Optional<SoftEntry<K, V>> entry = Optional.ofNullable(map.get(validateKey(key)));
    return entry.map(SoftEntry::getValue).orElse(null);
  }

  @Override
  public Map<K, V> getAll(final Set<? extends K> set) {
    throwIfClosed();
    return set.stream()
        .map(map::get)
        .filter(Predicates.notNull())
        .map(RefUtil::strongify)
        .filter(StrongEntry::hasValue)
        .collect(
            toMap(Entry::getKey,
                  Entry::getValue));
  }

  @Override
  public boolean containsKey(final K key) {
    throwIfClosed();
    return Optional.ofNullable(map.get(validateKey(key))).map(SoftReference::get).isPresent();
  }

  @Override
  public void put(final K k, final V v) {
    throwIfClosed();
    final Optional<V> oldValue = Optional.ofNullable(map.put(k, createEntry(k, v))).map(SoftEntry::getValue);
    notifyCreatedOrUpdated(k, v, oldValue);
  }

  @Override
  public V getAndPut(final K k, final V v) {
    throwIfClosed();
    final Optional<SoftEntry<K, V>> oldEntry = Optional.of(map.replace(k, createEntry(k, v)));
    notifyCreatedOrUpdated(k, v, oldEntry.map(SoftEntry::getValue));
    return oldEntry.map(SoftEntry::getValue).orElse(null);
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> map) {
    throwIfClosed();
    map.forEach(this::put);
  }

  @Override
  public boolean putIfAbsent(final K k, final V v) {
    throwIfClosed();
    final SoftEntry<K, V> oldEntry = map.putIfAbsent(k, createEntry(k, v));
    final boolean wasPut = !Optional.ofNullable(oldEntry)
        .map(RefUtil::strongify)
        .map(StrongEntry::getValue)
        .isPresent();
    if (wasPut) {
      notifyCreatedOrUpdated(k, v, Optional.empty());
    }
    return wasPut;
  }

  @Override
  public boolean remove(final K k) {
    throwIfClosed();
    final Optional<V> oldValue = Optional.ofNullable(map.remove(k))
        .map(RefUtil::strongify)
        .map(StrongEntry::getValue);
    if (oldValue.isPresent()) {
      notifyRemoved(k, oldValue.get());
    }
    return oldValue.isPresent();
  }

  @Override
  public boolean remove(final K k, final V v) {
    throwIfClosed();
    final boolean wasRemoved = map.remove(k, createEntry(k, v));
    if (wasRemoved) {
      notifyRemoved(k, v);
    }
    return wasRemoved;
  }

  @Override
  public V getAndRemove(final K k) {
    throwIfClosed();
    final V oldValue = Optional.ofNullable(map.remove(k)).map(SoftEntry::getValue).orElse(null);
    if (null != oldValue) {
      notifyRemoved(k, oldValue);
    }
    return oldValue;
  }

  @Override
  public boolean replace(final K k, final V oldValue, final V newValue) {
    throwIfClosed();
    final boolean wasReplaced = map.replace(k, createEntry(k, oldValue), createEntry(k, newValue));
    if (wasReplaced) {
      notifyCreatedOrUpdated(k, newValue, Optional.of(oldValue));
    }
    return wasReplaced;
  }

  @Override
  public boolean replace(final K key, final V newValue) {
    throwIfClosed();
    final Optional<V> oldValue = Optional.ofNullable(map.replace(key, createEntry(key, newValue)))
        .map(RefUtil::strongify)
        .map(StrongEntry::getValue);
    if (oldValue.isPresent()) {
      notifyCreatedOrUpdated(key, newValue, oldValue);
    }
    return oldValue.isPresent();
  }

  @Override
  public V getAndReplace(final K k, final V v) {
    throwIfClosed();
    final Optional<V> oldValue = Optional.ofNullable(map.replace(k, createEntry(k, v)))
        .map(RefUtil::strongify)
        .map(StrongEntry::getValue);
    if (oldValue.isPresent()) {
      notifyCreatedOrUpdated(k, v, oldValue);
    }
    return oldValue.orElse(null);
  }

  @Override
  public void removeAll(final Set<? extends K> set) {
    throwIfClosed();
    if (set.isEmpty()) {
      return;
    }
    set.stream().forEach(this::remove);
  }

  @Override
  public void removeAll() {
    throwIfClosed();
    Set<K> keys = map.keySet();
    keys.stream().forEach(this::remove);
  }

  @Override
  public void clear() {
    throwIfClosed();
    map.clear();
  }

  @Override
  public <C extends Configuration<K, V>> C getConfiguration(final Class<C> aClass) {
    return null;
  }

  @Override
  public <T> T invoke(final K k,
                      final EntryProcessor<K, V, T> entryProcessor,
                      final Object... objects) throws EntryProcessorException {
    throwIfClosed();
    checkNotNull(entryProcessor);
    final MutableEntry<K, V> mutableEntry = mutable(
        Optional.ofNullable(map.get(k))
            .map(RefUtil::strongify)
            .orElse(StrongEntry.create(k, null)));
    final T result = entryProcessor.process(mutableEntry, objects);
    processMutation(mutableEntry);
    return result;
  }

  private void processMutation(final MutableEntry<K, V> mutableEntry) {
    checkArgument(!(mutableEntry.isRemoved() && mutableEntry.isNewValueSet()),
                  "can't be removed and have new value at the same time for key: " + mutableEntry.getKey());
    if (mutableEntry.isRemoved()) {
      remove(mutableEntry.getKey());
    }
    if (mutableEntry.isNewValueSet()) {
      put(mutableEntry.getKey(), mutableEntry.getNewValue());
    }
  }

  @Override
  public <T> Map<K, EntryProcessorResult<T>> invokeAll(final Set<? extends K> keys,
                                                       final EntryProcessor<K, V, T> entryProcessor,
                                                       final Object... objects) {
    throwIfClosed();
    checkNotNull(keys);
    final ImmutableMap.Builder<K, EntryProcessorResult<T>> builder = ImmutableMap.builder();
    for (final K key : keys) {
      try {
        final T result = invoke(key, entryProcessor, objects);
        builder.put(key, () -> result);
      } catch (Exception passed) {
        builder.put(key, () -> { throw new EntryProcessorException(passed); });
      }
    }
    return builder.build();
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
    throwIfClosed();
    isClosed.set(true);
  }

  @Override
  public boolean isClosed() {
    return isClosed.get();
  }

  @Override
  public <T> T unwrap(final Class<T> aClass) {
    if (DcacheImpl.class == aClass || Dcache.class == aClass) {
      return (T) this;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public void registerCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    Preconditions.checkArgument(eventListener.isPresent());
    final CacheListenerAdapter<K, V> cacheListenerAdapter =
        CacheListenerAdapter.fromConfiguration(cacheEntryListenerConfiguration);
    eventListener.get().addListener(cacheListenerAdapter);
  }

  @Override
  public void deregisterCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    Preconditions.checkArgument(eventListener.isPresent());
    final CacheListenerAdapter<K, V> cacheListenerAdapter =
        CacheListenerAdapter.fromConfiguration(cacheEntryListenerConfiguration);
    eventListener.get().removeListener(cacheListenerAdapter);
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void loadAll(final Set<? extends K> keys,
                      final boolean reload,
                      final CompletionListener completionListener) {
    throw new UnsupportedOperationException();
  }

  private void notifyCreatedOrUpdated(final K key, final V newValue, final Optional<V> oldValue) {
    eventListener.ifPresent(l -> {
      final CacheEntryEvent<K, V> entryEvent = oldValue
          .map(old -> new CacheEntryEvent<>(this, EventType.UPDATED, key, newValue, old))
          .orElse(new CacheEntryEvent<>(this, EventType.CREATED, key, newValue, null));
      if (EventType.CREATED == entryEvent.getEventType()) {
        l.onCreated(Lists.newArrayList(entryEvent));
      } else {
        l.onUpdated(Lists.newArrayList(entryEvent));
      }
    });
  }

  private void notifyRemoved(final K key, final V oldValue) {
    eventListener.ifPresent(l ->
        l.onRemoved(Lists.newArrayList(new CacheEntryEvent<>(this, EventType.REMOVED, key, null, oldValue))));
  }

  private void throwIfClosed() {
    if (isClosed.get()) {
      throw new IllegalStateException("The cache is closed");
    }
  }

  private K validateKey(K key) {
    return checkNotNull(key, "key must be not null");
  }

  private V validateValue(V value) {
    return checkNotNull(value, "value must be not null");
  }

  private SoftEntry<K, V> createEntry(final K k, final V v) {
    return SoftEntry.create(validateKey(k), validateValue(v));
  }

}
