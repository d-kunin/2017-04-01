package ru.otus.messageSystem;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

  public static Address create(String id) {
    return new AutoValue_Address(id);
  }

  public abstract String id();

}
