package net.kundzi.messagesystem;

import com.google.auto.value.AutoValue;
import net.kundzi.messagesystem.protocol.Address;


@AutoValue
public abstract class MessageSystemContext {

  public static MessageSystemContext.Builder builder() {
    return new net.kundzi.messagesystem.AutoValue_MessageSystemContext.Builder();
  }

  public abstract String serverHostname();

  public abstract int serverPort();

  public abstract Address cacheAddress();

  public abstract Address frontendAddress();

  @AutoValue.Builder
  public interface Builder {

    Builder serverHostname(String hostname);

    Builder serverPort(int port);

    Builder cacheAddress(Address address);

    Builder frontendAddress(Address frontendAddress);

    MessageSystemContext build();
  }

}
