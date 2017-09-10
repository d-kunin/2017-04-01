package ru.otus.kunin.messageSystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

  public final static Address UPSTREAM = create("upstream");

  @JsonCreator
  public static Address create(@JsonProperty("id") String id) {
    return new ru.otus.kunin.messageSystem.AutoValue_Address(id);
  }

  @JsonProperty("id")
  public abstract String id();

}
