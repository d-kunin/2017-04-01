package ru.otus.kunin.message2;

import com.google.auto.value.AutoValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kundzi.socket.channels.message.lvmessage.LvMessage;
import ru.otus.kunin.messageSystem.Address;

import javax.annotation.Nullable;

@AutoValue
public abstract class MessageV2 implements LvMessage {

  @JsonCreator
  public static MessageV2 create(
      @JsonProperty("id") String id,
      @JsonProperty("in_response_to") String inResponseTo,
      @JsonProperty("type") String type,
      @JsonProperty("from") Address from,
      @JsonProperty("to") Address to,
      @JsonProperty("payload") JsonNode payload
  ) {
    return new ru.otus.kunin.message2.AutoValue_MessageV2(id, inResponseTo, type, from, to, payload);
  }

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String VERSION = "v1";

  @JsonProperty("id")
  public abstract String id();

  @Nullable
  @JsonProperty("in_response_to")
  public abstract String inResponseTo();

  @JsonProperty("type")
  public abstract String type();

  @JsonProperty("from")
  public abstract Address from();

  @Nullable
  @JsonProperty("to")
  public abstract Address to();

  @Nullable
  @JsonProperty("payload")
  public abstract JsonNode payload();

  @JsonProperty("version")
  public String version() {
    return VERSION;
  }

  @JsonIgnore
  @Override
  public byte[] data() {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Did not expect that :(", e);
    }
  }

  @JsonIgnore
  @Override
  public int length() {
    // This is not the most effective.
    // But since fields are not guaranteed to be immutable it is a safer option.
    return data().length;
  }
}
