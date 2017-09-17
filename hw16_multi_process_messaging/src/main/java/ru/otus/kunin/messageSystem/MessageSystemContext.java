package ru.otus.kunin.messageSystem;

import com.google.auto.value.AutoValue;

import java.net.InetSocketAddress;

@AutoValue
public abstract class MessageSystemContext {

  public static MessageSystemContext.Builder builder() {
    return new ru.otus.kunin.messageSystem.AutoValue_MessageSystemContext.Builder();
  }

  public abstract InetSocketAddress serverSocketAddress();

  public abstract Address cacheAddress();

  public abstract Address frontendAddress();

  @AutoValue.Builder
  public interface Builder {

    Builder serverSocketAddress(InetSocketAddress inetSocketAddress);

    Builder cacheAddress(Address address);

    Builder frontendAddress(Address frontendAddress);

    MessageSystemContext build();
  }

}
