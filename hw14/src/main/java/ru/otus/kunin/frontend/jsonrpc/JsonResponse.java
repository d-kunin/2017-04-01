package ru.otus.kunin.frontend.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoValue
public abstract class JsonResponse {

  public static JsonResponse create(@Nullable Object data,
                                    @Nullable String error,
                                    @Nonnull String requestId) {
    return new AutoValue_JsonResponse(data, error, requestId);
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
