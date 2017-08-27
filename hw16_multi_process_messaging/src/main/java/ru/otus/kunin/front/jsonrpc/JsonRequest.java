package ru.otus.kunin.front.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.value.AutoValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@AutoValue
public abstract class JsonRequest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger LOG = LoggerFactory.getLogger(JsonRequest.class);

  public static JsonRequest fromJson(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, JsonRequest.class);
    } catch (IOException e) {
      LOG.error("Failed to parse json", e);
      return null;
    }
  }

  @Nonnull
  public abstract String id();

  @Nonnull
  public abstract String method();

  @Nullable
  public abstract ObjectNode params();

  @JsonCreator
  public static JsonRequest create(@JsonProperty("id") String id,
                                   @JsonProperty("method") String method,
                                   @JsonProperty("params") ObjectNode params)
  {
    return new AutoValue_JsonRequest(id, method, params);
  }
}
