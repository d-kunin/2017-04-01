package ru.otus.kunin.dson.tools;

import com.google.common.base.Preconditions;
import java.util.IdentityHashMap;
import ru.otus.kunin.dson.IdentitySet;

public class IdentitySetImpl implements IdentitySet {

  private final IdentityHashMap map;

  public IdentitySetImpl() {
    this(new IdentityHashMap());
  }

  private IdentitySetImpl(final IdentityHashMap map) {
    this.map = Preconditions.checkNotNull(map);
  }

  @Override
  public void add(final Object o) {
    map.put(o, null);
  }

  @Override
  public boolean contains(final Object o) {
    return map.containsKey(o);
  }

  @Override
  public IdentitySet copy() {
    return new IdentitySetImpl(new IdentityHashMap(map));
  }

  @Override
  public int size() {
    return map.size();
  }
}
