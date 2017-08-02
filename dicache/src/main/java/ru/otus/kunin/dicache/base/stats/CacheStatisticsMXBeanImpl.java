package ru.otus.kunin.dicache.base.stats;

import javax.cache.management.CacheStatisticsMXBean;
import java.util.concurrent.atomic.AtomicLong;

public class CacheStatisticsMXBeanImpl implements CacheStatisticsMXBean {

  private final AtomicLong put = new AtomicLong(0);
  private final AtomicLong get = new AtomicLong(0);
  private final AtomicLong miss = new AtomicLong(0);
  private final AtomicLong hit = new AtomicLong(0);
  private final AtomicLong remove = new AtomicLong(0);
  private final AtomicLong evict = new AtomicLong(0);

  @Override
  public void clear() {
    miss.set(0);
    hit.set(0);
    get.set(0);
    put.set(0);
    remove.set(0);
    evict.set(0);
  }

  @Override
  public long getCacheHits() {
    return hit.get();
  }

  public void onHit() {
    hit.incrementAndGet();
  }

  @Override
  public long getCacheMisses() {
    return miss.get();
  }

  public void onMiss() {
    miss.incrementAndGet();
  }

  @Override
  public float getCacheHitPercentage() {
    if (0 == getCacheGets()) {
      return 0;
    }
    return 100f * getCacheHits() / getCacheGets();
  }

  @Override
  public float getCacheMissPercentage() {
    if (0 == getCacheGets()) {
      return 0;
    }
    return 100f * getCacheMisses() / getCacheGets();
  }

  @Override
  public long getCacheGets() {
    return get.get();
  }

  public void onGet() {
    get.incrementAndGet();
  }

  @Override
  public long getCachePuts() {
    return put.get();
  }

  public void onPut() {
    put.incrementAndGet();
  }

  @Override
  public long getCacheRemovals() {
    return remove.get();
  }

  public void onRemove() {
    remove.incrementAndGet();
  }

  @Override
  public long getCacheEvictions() {
    return evict.get();
  }

  public void onEvict() {
    evict.incrementAndGet();
  }

  // Won't do

  @Override
  public float getAverageGetTime() {
    return -1;
  }

  @Override
  public float getAveragePutTime() {
    return -1;
  }

  @Override
  public float getAverageRemoveTime() {
    return -1;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CacheStatisticsMXBeanImpl{");
    sb.append("put=").append(put);
    sb.append(", get=").append(get);
    sb.append(", miss=").append(miss);
    sb.append(", hit=").append(hit);
    sb.append(", remove=").append(remove);
    sb.append(", evict=").append(evict);
    sb.append('}');
    return sb.toString();
  }
}
