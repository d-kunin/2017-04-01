package ru.otus.kunin.dicache.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import ru.otus.kunin.dicache.base.entry.MutableEntry;

import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import javax.cache.processor.EntryProcessorResult;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DiCacheTest {

  final DiCache<String, String> cache = new DiCache<>();
  final StoringCacheEventListener<String, String> eventListener = new StoringCacheEventListener<>();

  @Before
  public void setUp() throws Exception {
    cache.putAll(ImmutableMap.<String, String>builder()
      .put("k1", "v1")
      .put("k2", "v2")
      .put("k3", "v3")
      .build());
  }

  @Test
  public void testGet() throws Exception {
    assertEquals("v1", cache.get("k1"));
  }

  @Test
  public void testGetAll() throws Exception {
    final Map<String, String> all =
        cache.getAll(Sets.newHashSet("k1", "k2", "key_is_not_present"));
    assertEquals(2, all.size());
    assertEquals("v1", all.get("k1"));
    assertEquals("v2", all.get("k2"));
  }

  @Test
  public void testContainsKey() throws Exception {
    assertTrue(cache.containsKey("k1"));
    assertFalse(cache.containsKey("java_script_and_smoothy"));
  }

  @Test
  public void testPut() throws Exception {
    cache.put("no_sql", "skinny_jeans");
    assertEquals("skinny_jeans", cache.get("no_sql"));
  }

  @Test
  public void testGetAndPut() throws Exception {
    final String oldValue = cache.getAndPut("k1", "new_v1");
    assertEquals("v1", oldValue);
    assertEquals("new_v1", cache.get("k1"));
  }

  @Test
  public void testPutAll() throws Exception {
    cache.putAll(ImmutableMap.of(
        "k4", "v4",
        "k5", "v5"
    ));
    assertEquals("v4", cache.get("k4"));
    assertEquals("v5", cache.get("k5"));
  }

  @Test
  public void testPutIfAbsent() throws Exception {
    cache.putIfAbsent("k1", "will_not_be_put");
    cache.putIfAbsent("k4", "v4");
    assertEquals("v1", cache.get("k1"));
    assertEquals("v4", cache.get("k4"));
  }

  @Test
  public void testRemove() throws Exception {
    cache.remove("k1");
    assertEquals(null, cache.get("k1"));
  }

  @Test
  public void testRemoveIfEquals() throws Exception {
    cache.remove("k1", "v1");
    cache.remove("k2", "god_save_sql");
    assertFalse(cache.containsKey("k1"));
    assertEquals("v2", cache.get("k2"));
  }

  @Test
  public void testGetAndRemove() throws Exception {
    final String v1 = cache.getAndRemove("k1");
    assertFalse(cache.containsKey("k1"));
    assertEquals("v1", v1);
  }

  @Test
  public void testReplace() throws Exception {
    cache.replace("k1", "new_v1");
    cache.replace("k4", "will_not_be_there");
    assertEquals("new_v1", cache.get("k1"));
    assertEquals(null, cache.get("k4"));
  }

  @Test
  public void testReplaceIfEquals() throws Exception {
    cache.replace("k1", "v1", "new_v1");
    cache.replace("k2", "nuts", "will_not_be_there");
    assertEquals("new_v1", cache.get("k1"));
    assertEquals("v2", cache.get("k2"));
  }

  @Test
  public void testGetAndReplace() throws Exception {
    final String oldValue = cache.getAndReplace("k1", "new_v1");
    assertEquals("v1", oldValue);
    assertEquals("new_v1", cache.get("k1"));
  }

  @Test
  public void testRemoveAll() throws Exception {
    cache.removeAll(Sets.newHashSet("k1", "k2", "banan"));
    assertFalse(cache.containsKey("k1"));
    assertFalse(cache.containsKey("k2"));
  }

  @Test
  public void testRemoveAllEveryKey() throws Exception {
    cache.removeAll();
    assertFalse(cache.containsKey("k1"));
    assertFalse(cache.containsKey("k2"));
    assertFalse(cache.containsKey("k3"));
  }

  @Test
  public void testClear() throws Exception {
    cache.clear();
    assertFalse(cache.containsKey("k1"));
    assertFalse(cache.containsKey("k2"));
    assertFalse(cache.containsKey("k3"));
  }

  @Test
  public void testInvoke() throws Exception {
    final int result = cache.invoke(
        "k1",
        (entry, arguments) -> {
          final String newValue = entry.getValue() + arguments[0];
          assertEquals("v1", entry.getValue());
          entry.setValue(newValue);
          final MutableEntry unwrap = entry.unwrap(MutableEntry.class);
          assertEquals("v1postfix", unwrap.getValue());
          return newValue.length();
        },
        "postfix");
    assertEquals(9, result);
    assertEquals("v1postfix", cache.get("k1"));

    cache.invoke(
        "k2",
        (entry, arguments) -> {
          final MutableEntry unwrap = entry.unwrap(MutableEntry.class);
          unwrap.remove();
          assertEquals(null, entry.getValue());
          assertTrue(unwrap.isRemoved());
          return "socks";
        },
        null);
    assertFalse(cache.containsKey("k2"));
  }

  @Test
  public void testInvokeAll() throws Exception {
    final Map<String, EntryProcessorResult<String>> results = cache.invokeAll(
        Sets.newHashSet("k1", "k2"),
        (entry, arguments) -> {
          entry.setValue("new_" + entry.getValue());
          return entry.getValue();
        },
        null);
    assertEquals("v3", cache.get("k3"));
    assertEquals("new_v2", cache.get("k2"));
    assertEquals("new_v2", results.get("k2").get());
    assertEquals("new_v1", cache.get("k1"));
    assertEquals("new_v1", results.get("k1").get());
  }

  @Test
  public void testClose() throws Exception {
    assertFalse(cache.isClosed());
    cache.close();
    assertTrue(cache.isClosed());
    try {
      cache.get("k1");
      fail();
    } catch (IllegalStateException expected){
    }
  }

  @Test
  public void testUnwrap() throws Exception {
    final DiCache unwrap = cache.unwrap(DiCache.class);
    assertEquals(unwrap, cache);

    try {
      cache.unwrap(List.class);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testEvents() throws Exception {
    cache.put("keepToExpire", "give_me_more_of_this_hipster_smoothies");
    final MutableCacheEntryListenerConfiguration<String, String> listenerConfiguration =
        new MutableCacheEntryListenerConfiguration<>(
        () -> eventListener,
        () -> event -> event.getKey().startsWith("keep"),
        false,
        false
    );
    cache.registerCacheEntryListener(listenerConfiguration);
    cache.put("keep1", "v1");
    cache.put("will_be_filtered_1", "v1");

    {
      final List<CacheEntryEvent<? extends String, ? extends String>> events = eventListener.getEvents();
      assertEquals(1, events.size());
      final CacheEntryEvent<? extends String, ? extends String> createEvent = events.get(0);
      assertEquals("keep1", createEvent.getKey());
      assertEquals(EventType.CREATED, createEvent.getEventType());
    }

    {
      cache.put("keep1", "update_value");
      final List<CacheEntryEvent<? extends String, ? extends String>> eventsAfterUpdate = eventListener.getEvents();
      assertEquals(2, eventsAfterUpdate.size());
      final CacheEntryEvent<? extends String, ? extends String> updateEvent = eventsAfterUpdate.get(1);
      assertEquals("keep1", updateEvent.getKey());
      assertEquals(EventType.UPDATED, updateEvent.getEventType());
    }

    {
      cache.remove("keep1");
      final List<CacheEntryEvent<? extends String, ? extends String>> eventsAfterRemove = eventListener.getEvents();
      assertEquals(3, eventsAfterRemove.size());
      final CacheEntryEvent<? extends String, ? extends String> removeEvent = eventsAfterRemove.get(2);
      assertEquals("keep1", removeEvent.getKey());
      assertEquals(EventType.REMOVED, removeEvent.getEventType());
    }

    {
      cache.expireEntry("keepToExpire");
      Thread.sleep(100);
      final List<CacheEntryEvent<? extends String, ? extends String>> eventsAfterCleanUp = eventListener.getEvents();
      assertEquals(4, eventsAfterCleanUp.size());
      final CacheEntryEvent<? extends String, ? extends String> expiredEvent = eventsAfterCleanUp.get(3);
      assertEquals("keepToExpire", expiredEvent.getKey());
      assertEquals(EventType.EXPIRED, expiredEvent.getEventType());
    }
  }
}