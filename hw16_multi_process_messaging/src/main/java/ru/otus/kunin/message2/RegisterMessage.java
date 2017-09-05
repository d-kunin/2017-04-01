package ru.otus.kunin.message2;

import com.google.auto.value.AutoValue;
import ru.otus.kunin.messageSystem.Address;

@AutoValue
public abstract class RegisterMessage implements MessageV2 {

  public static RegisterMessage create(Address from, Address to) {
    return new ru.otus.kunin.message2.AutoValue_RegisterMessage(
        UuidGenerator.nextUuid(),
        null,
        MessageTypes.TYPE_REGISTER,
        from,
        to
    );
  }

}
