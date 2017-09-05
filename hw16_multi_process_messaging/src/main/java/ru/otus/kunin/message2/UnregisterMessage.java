package ru.otus.kunin.message2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;
import ru.otus.kunin.messageSystem.Address;

@AutoValue
public abstract class UnregisterMessage implements MessageV2 {

  public static UnregisterMessage create(Address from, Address to) {
    return new ru.otus.kunin.message2.AutoValue_UnregisterMessage(
        UuidGenerator.nextUuid(),
        null,
        MessageTypes.TYPE_UNREGISTER,
        from,
        to
    );
  }

  @JsonCreator
  public static UnregisterMessage create(
      String id,
      String inResponseToId,
      String type,
      Address from,
//      Address to
  ) {
    return new ru.otus.kunin.message2.AutoValue_UnregisterMessage(
        id,
        inResponseToId,
        type,
        from,
        to
    );
  }

}
