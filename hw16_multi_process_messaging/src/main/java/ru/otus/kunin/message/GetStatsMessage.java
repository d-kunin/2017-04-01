package ru.otus.kunin.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.Address;

import javax.cache.management.CacheStatisticsMXBean;

public class GetStatsMessage {

  public static final String TOPIC_GET_STATS = "get_stats";

  @AutoValue
  public static abstract class PayloadResponse {

    @JsonProperty("stats")
    public abstract CacheStatisticsMXBean stats();

    @JsonCreator
    public static PayloadResponse create(@JsonProperty("stats") CacheStatisticsMXBean stats) {
      return new ru.otus.kunin.message.AutoValue_GetStatsMessage_PayloadResponse(stats);
    }

  }

  public static MessageV2 createRequest(Address from, Address to) {
    return MessageV2.createRequest(TOPIC_GET_STATS,
                                   from,
                                   to,
                                   null);
  }

  public static MessageV2 createResponse(MessageV2 request, CacheStatisticsMXBean stats) {
    return request.createResponseMessage(200, MessageV2.asPayload(PayloadResponse.create(stats)));
  }
}
