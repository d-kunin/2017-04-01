package ru.otus.kunin.message2;

import com.google.auto.value.AutoValue;
import ru.otus.kunin.messageSystem.Address;

@AutoValue
public abstract class GetStatsMessage implements MessageV2 {

  public final static String TYPE = GetStatsMessage.class.getSimpleName();

  @Override
  public String type() {
    return TYPE;
  }

  public static GetStatsMessage create(Address from, Address to /* is not it fixed */) {
    return new ru.otus.kunin.message2.AutoValue_GetStatsMessage(from, to);
  }
}
