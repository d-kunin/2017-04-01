package ru.otus.kunin.messageSystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

  @JsonCreator
  public static Address create(String id) {
    return new ru.otus.kunin.messageSystem.AutoValue_Address(id);
  }

  @JsonProperty("id")
  public abstract String id();

}
