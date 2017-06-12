package ru.otus.kunin.dson.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ru.otus.kunin.dson.IdentitySet;

public class IdentitySetImplTest {

  IdentitySetImpl identitySet = new IdentitySetImpl();

  @Test
  public void add() throws Exception {
    assertEquals(0, identitySet.size());
    identitySet.add("hello");
    assertEquals(1, identitySet.size());
  }

  @Test
  public void contains() throws Exception {
    identitySet.add(new String("one"));
    assertFalse(identitySet.contains("one"));
    identitySet.add("one");
    assertTrue(identitySet.contains("one"));
    assertEquals(2, identitySet.size());
  }

  @Test
  public void copy() throws Exception {
    Object o = new Object();
    identitySet.add(o);
    IdentitySet copy = identitySet.copy();
    assertEquals(identitySet.size(), copy.size());
    assertTrue(identitySet.contains(o));
    assertTrue(copy.contains(o));

    Object o1 = new Object();
    copy.add(o1);
    assertTrue(copy.contains(o1));
    assertFalse(identitySet.contains(o1));

    Object o2 = new Object();
    identitySet.add(o2);
    assertFalse(copy.contains(o2));
    assertTrue(identitySet.contains(o2));
  }

}