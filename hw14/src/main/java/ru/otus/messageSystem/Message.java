package ru.otus.messageSystem;

import ru.otus.kunin.app.AddressableCache;
import ru.otus.kunin.app.AddressableJsonRequest;

/**
 * @author tully
 */
public abstract class Message {
  private final Address from;
  private final Address to;

  public Message(Address from, Address to) {
    this.from = from;
    this.to = to;
  }

  public Address getFrom() {
    return from;
  }

  public Address getTo() {
    return to;
  }

  public void exec(AddressableCache addressableCache) {
  }

  public void exec(final AddressableJsonRequest addressableJsonRequest) {
  }
}
