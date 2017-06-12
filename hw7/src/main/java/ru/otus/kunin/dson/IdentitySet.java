package ru.otus.kunin.dson;

public interface IdentitySet {

  void add(Object o);

  boolean contains(Object o);

  IdentitySet copy();

  int size();

}
