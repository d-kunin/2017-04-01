package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.otus.kunin.messageSystem.Address;

public interface MessageV2 {

  // could also be: id(), timestamp()

  @JsonProperty("type")
  String type();

  @JsonProperty("from")
  Address from();

  @JsonProperty("to")
  Address to();

}
