package net.kundzi.messagesystem.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

  public final static Address UPSTREAM = create("upstream");

  @JsonCreator
  public static Address create(@JsonProperty("id") String id) {
    return new net.kundzi.messagesystem.protocol.AutoValue_Address(id);
  }

  @JsonProperty("id")
  public abstract String id();

}
