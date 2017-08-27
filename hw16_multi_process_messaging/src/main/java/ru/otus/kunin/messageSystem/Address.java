package ru.otus.kunin.messageSystem;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

  public static Address create(String id) {
    return new ru.otus.kunin.messageSystem.AutoValue_Address(id);
  }

  public abstract String id();

}
