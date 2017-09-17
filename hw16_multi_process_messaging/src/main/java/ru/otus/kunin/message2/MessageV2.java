package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;
import net.kundzi.socket.channels.message.lvmessage.LvMessage;
import ru.otus.kunin.messageSystem.Address;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

@AutoValue
public abstract class MessageV2 implements LvMessage {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static MessageV2 create(
      @Nullable String inResponseTo,
      @Nullable Integer statusCode,
      String type,
      Address from,
      Address to,
      @Nullable JsonNode payload
  ) {
    return new ru.otus.kunin.message2.AutoValue_MessageV2(
        UuidGenerator.nextUuid(), inResponseTo, statusCode, type, from, to, payload);
  }

  public static MessageV2 createRequest(
      String type,
      Address from,
      Address to,
      @Nullable JsonNode payload
  ) {
    return new ru.otus.kunin.message2.AutoValue_MessageV2(
        UuidGenerator.nextUuid(), null, null, type, from, to, payload);
  }

  @JsonCreator
  public static MessageV2 create(
      @JsonProperty("id") String id,
      @Nullable @JsonProperty("in_response_to") String inResponseTo,
      @Nullable @JsonProperty("status_code") Integer statusCode,
      @JsonProperty("topic") String topic,
      @JsonProperty("from") Address from,
      @JsonProperty("to") Address to,
      @Nullable @JsonProperty("payload") JsonNode payload
  ) {
    return new ru.otus.kunin.message2.AutoValue_MessageV2(id, inResponseTo, statusCode, topic, from, to, payload);
  }

  @JsonProperty("id")
  public abstract String id();

  @Nullable
  @JsonProperty("in_response_to")
  public abstract String inResponseTo();

  @Nullable
  @JsonProperty("status_code")
  public abstract Integer statusCode();

  @JsonProperty("topic")
  public abstract String topic();

  @JsonProperty("from")
  public abstract Address from();

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

  public <T> T typedPayload(Class<T> payloadClass) {
    if (payload() == null) {
      return null;
    }
    return fromPayload(payload(), payloadClass);
  }

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

  public MessageV2 createResponseMessage(int statusCode, JsonNode payload) {
    return create(this.id(),
                  statusCode,
                  topic(),
                  to(),
                  from(),
                  payload);
  }

  public static JsonNode asPayload(Object object) {
    return OBJECT_MAPPER.valueToTree(object);
  }

  public static <T> T fromPayload(JsonNode payload, Class<T> payloadClass) {
    try {
      return OBJECT_MAPPER.treeToValue(payload, payloadClass);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}
