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
import java.io.IOException;
import java.util.Optional;

@AutoValue
public abstract class MessageV2 implements LvMessage {

  public static Optional<MessageV2> fromJsonBytes(byte[] data) {
    final MessageV2 readValue;
    try {
      readValue = OBJECT_MAPPER.readValue(data, MessageV2.class);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    return Optional.of(readValue);
  }

  @JsonCreator
  public static MessageV2 create(
      @JsonProperty("id") String id,
      @Nullable @JsonProperty("in_response_to") String inResponseTo,
      @Nullable @JsonProperty("status_code") Integer statusCode,
      @JsonProperty("type") String type,
      @JsonProperty("from") Address from,
      @Nullable @JsonProperty("to") Address to,
      @Nullable @JsonProperty("payload") JsonNode payload
  ) {
    return new ru.otus.kunin.message2.AutoValue_MessageV2(id, inResponseTo, statusCode, type, from, to, payload);
  }

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @JsonProperty("id")
  public abstract String id();

  @Nullable
  @JsonProperty("in_response_to")
  public abstract String inResponseTo();

  @Nullable
  @JsonProperty("status_code")
  public abstract Integer statusCode();

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

  public MessageV2 createResponseMessage(int statusCode, JsonNode payload) {
    return create(UuidGenerator.nextUuid(),
                  this.id(),
                  statusCode,
                  MessageTypes.TYPE_RESPONSE,
                  to(),
                  from(),
                  payload);
  }
}
