package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import ru.otus.kunin.messageSystem.Address;

@AutoValue
public abstract class AddToCacheMessage implements MessageV2 {

  @JsonProperty("key")
  public abstract String key();

  @JsonProperty("value")
  public abstract String value();

  public final static String TYPE = AddToCacheMessage.class.getSimpleName();

  @Override
  public String type() {
    return TYPE;
  }

  public static AddToCacheMessage create(Address from, Address to /* is not it fixed */, String key, String value) {
    return new ru.otus.kunin.message2.AutoValue_AddToCacheMessage(from, to, key, value);
  }
}
