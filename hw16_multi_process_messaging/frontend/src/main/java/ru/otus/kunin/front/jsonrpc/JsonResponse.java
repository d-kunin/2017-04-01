package ru.otus.kunin.front.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@AutoValue
public abstract class JsonResponse {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger LOG = LoggerFactory.getLogger(JsonResponse.class);

  public String toJson() {
    try {
      return OBJECT_MAPPER.writeValueAsString(this);
    } catch (IOException e) {
      LOG.error("Failed to serializa to json", e);
      return null;
    }
  }

  public static JsonResponse create(@Nullable Object data,
                                    @Nullable String error,
                                    @Nonnull String requestId) {
    return new ru.otus.kunin.front.jsonrpc.AutoValue_JsonResponse(data, error, requestId);
  }

  @JsonProperty("result")
  @Nullable
  public abstract Object data();

  @JsonProperty("error")
  @Nullable
  public abstract String error();

  @JsonProperty("id")
  @Nonnull
  public abstract String requestId();

}
