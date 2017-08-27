package ru.otus.kunin.messageSystem;

import com.google.auto.value.AutoValue;

/**
 * Created by tully.
 */
@AutoValue
public abstract class MessageSystemContext {

  public static MessageSystemContext.Builder builder() {
    return new ru.otus.kunin.messageSystem.AutoValue_MessageSystemContext.Builder();
  }

  public abstract MessageSystem messageSystem();

  public abstract Address cacheAddress();

  @AutoValue.Builder
  public interface Builder {

    Builder messageSystem(MessageSystem messageSystem);

    Builder cacheAddress(Address address);

    MessageSystemContext build();
  }

}
