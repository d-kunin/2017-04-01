package ru.otus.messageSystem;

import com.google.auto.value.AutoValue;

/**
 * Created by tully.
 */
@AutoValue
public abstract class MessageSystemContext {

  public static MessageSystemContext.Builder builder() {
    return new AutoValue_MessageSystemContext.Builder();
  }

  public abstract MessageSystem messageSystem();

  public abstract Address frontendAddress();

  public abstract Address cacheAddress();

  @AutoValue.Builder
  interface Builder {

    Builder messageSystem(MessageSystem messageSystem);

    Builder frontendAddress(Address address);

    Builder cacheAddress(Address address);

    MessageSystemContext build();
  }

}
