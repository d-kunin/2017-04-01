package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import ru.otus.kunin.messageSystem.Address;

@AutoValue
public abstract class ResponseMessage<T> implements MessageV2 {

  @JsonProperty("result")
  public abstract T result();

  public final static String TYPE = ResponseMessage.class.getSimpleName();

  @Override
  public String type() {
    return TYPE;
  }

  public static <T> ResponseMessage<T> create(Address from, Address to /* is not it fixed */, T result) {
    return new ru.otus.kunin.message2.AutoValue_ResponseMessage<>(from, to, result);
  }
}
