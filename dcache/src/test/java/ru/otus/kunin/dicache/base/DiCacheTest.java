package ru.otus.kunin.dicache.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiCacheTest {

  final DiCache<String, String> cache = new DiCache<>();

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
  }

  @Test
  public void testRemoveAll1() throws Exception {
  }

  @Test
  public void testClear() throws Exception {
  }

  @Test
  public void testInvoke() throws Exception {
  }

  @Test
  public void testInvokeAll() throws Exception {
  }

  @Test
  public void testClose() throws Exception {
  }

  @Test
  public void testIsClosed() throws Exception {
  }

  @Test
  public void testUnwrap() throws Exception {
  }

  @Test
  public void testRegisterCacheEntryListener() throws Exception {
  }

  @Test
  public void testDeregisterCacheEntryListener() throws Exception {
  }

  @Test
  public void testRegisterCacheEntryListener1() throws Exception {
  }

}