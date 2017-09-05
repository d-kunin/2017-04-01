package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kundzi.socket.channels.message.lvmessage.LvMessage;
import ru.otus.kunin.messageSystem.Address;

import javax.annotation.Nullable;

public interface MessageV2 extends LvMessage {

  // Constants
  @JsonIgnore
  ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @JsonIgnore
  String VERSION = "v1";

  @JsonProperty("id")
  String id();

  // Properties
  @Nullable
  @JsonProperty("in_response_to_id")
  String inResponseToId();

  @JsonProperty("type")
  String type();

  @JsonProperty("from")
  Address from();

  @Nullable
  @JsonProperty("to")
  Address to();

  // Default methods
  @JsonProperty("version")
  default String version() {
    return VERSION;
  }

  @JsonIgnore
  @Override
  default byte[] data() {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Did not expect that :(", e);
    }
  }

  @JsonIgnore
  @Override
  default int length() {
    // TODO not effective at all, please fix
    // Though in practice length and data are called once, so it will not be as bad.
    return data().length;
  }
}
