package ru.otus.kunin.messageSystem;

import ru.otus.kunin.message2.MessageV2;

import java.io.Closeable;
import java.util.function.Consumer;

public abstract class MessageClient implements Closeable {

  private Consumer<MessageV2> listener;

  private Address address;

  public Address getAddress() {
    return address;
  }

  public void setOnMessageListener(Consumer<MessageV2> listener) {
    this.listener = listener;
  }

  protected void deliver(MessageV2 messageV2) {
    final Consumer<MessageV2> listenerRef = this.listener;
    if (null != listenerRef) {
      listenerRef.accept(messageV2);
    }
  }

  public abstract void send(MessageV2 messageV2);

}
