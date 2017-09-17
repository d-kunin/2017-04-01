package ru.otus.kunin.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.Address;

import javax.annotation.Nullable;

public class GetFromCacheMessage {

  public static final String TOPIC_GET_FROM_CACHE = "get_from_cache";

  @AutoValue
  public static abstract class PayloadRequest {

    @JsonProperty("key")
    public abstract String key();

    @JsonCreator
    public static PayloadRequest create(@JsonProperty("key") String key) {
      return new ru.otus.kunin.message.AutoValue_GetFromCacheMessage_PayloadRequest(key);
    }
  }

  @AutoValue
  public static abstract class PayloadResponse {

    @JsonProperty("key")
    public abstract String key();

    @Nullable
    @JsonProperty("value")
    public abstract String value();

    @JsonCreator
    public static PayloadResponse create(@JsonProperty("key") String key,
                                         @JsonProperty("value") String value) {
      return new ru.otus.kunin.message.AutoValue_GetFromCacheMessage_PayloadResponse(key, value);
    }

  }

  public static MessageV2 createRequest(Address from, Address to, String key) {
    return MessageV2.createRequest(TOPIC_GET_FROM_CACHE,
                                   from,
                                   to,
                                   MessageV2.asPayload(PayloadRequest.create(key)));
  }

  public static MessageV2 createResponse(MessageV2 request, String key, String value) {
    return request.createResponseMessage(200, MessageV2.asPayload(PayloadResponse.create(key, value)));
  }
}
