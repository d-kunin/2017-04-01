package ru.otus.kunin.dcache.impl;

import ru.otus.kunin.dcache.Dcache;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DcacheImpl<K, V> implements Dcache<K, V> {

  @Override
  public V get(final K k) {
    return null;
  }

  @Override
  public Map<K, V> getAll(final Set<? extends K> set) {
    return null;
  }

  @Override
  public boolean containsKey(final K k) {
    return false;
  }

  @Override
  public void loadAll(final Set<? extends K> set, final boolean b, final CompletionListener completionListener) {

  }

  @Override
  public void put(final K k, final V v) {

  }

  @Override
  public V getAndPut(final K k, final V v) {
    return null;
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> map) {

  }

  @Override
  public boolean putIfAbsent(final K k, final V v) {
    return false;
  }

  @Override
  public boolean remove(final K k) {
    return false;
  }

  @Override
  public boolean remove(final K k, final V v) {
    return false;
  }

  @Override
  public V getAndRemove(final K k) {
    return null;
  }

  @Override
  public boolean replace(final K k, final V v, final V v1) {
    return false;
  }

  @Override
  public boolean replace(final K k, final V v) {
    return false;
  }

  @Override
  public V getAndReplace(final K k, final V v) {
    return null;
  }

  @Override
  public void removeAll(final Set<? extends K> set) {

  }

  @Override
  public void removeAll() {

  }

  @Override
  public void clear() {

  }

  @Override
  public <C extends Configuration<K, V>> C getConfiguration(final Class<C> aClass) {
    return null;
  }

  @Override
  public <T> T invoke(final K k, final EntryProcessor<K, V, T> entryProcessor, final Object... objects) throws EntryProcessorException {
    return null;
  }

  @Override
  public <T> Map<K, EntryProcessorResult<T>> invokeAll(final Set<? extends K> set, final EntryProcessor<K, V, T> entryProcessor, final Object... objects) {
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

}
