package ru.otus.kunin.messageSystem;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MessageSystem implements Closeable {

  private static final long DEFAULT_STEP_TIME_MS = 10;
  private final ConcurrentHashMap<Address, ConcurrentLinkedQueue<MessageOld>> messagesMap = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Address, Addressee> addresseeMap = new ConcurrentHashMap<>();
  private final AtomicBoolean isActive = new AtomicBoolean(true);

//  public void register(ClientConnection clientConnection) {
//
//  }
//
//  public void unregister();
//
//  public void route();

  public void addAddressee(Addressee addressee) {
    addresseeMap.put(addressee.getAddress(), addressee);
    messagesMap.put(addressee.getAddress(), new ConcurrentLinkedQueue<>());
  }

  public void removeAddressee(Addressee addressee) {
    addresseeMap.remove(addressee.getAddress());
    messagesMap.remove(addressee.getAddress());
  }

  public void sendMessage(MessageOld messageOld) {
    messagesMap.get(messageOld.getTo()).add(messageOld);
  }

  public void stop() {
    isActive.set(false);
  }

  public void start() {
    new Thread(() -> {
      while (isActive.get()) {
        for (Map.Entry<Address, Addressee> entry : addresseeMap.entrySet()) {
          final Addressee addressee = entry.getValue();
          final Address address = entry.getKey();
          final ConcurrentLinkedQueue<MessageOld> queue = messagesMap.get(address);
          processQueue(addressee, queue);
          sleep();
        }
      }
    }).start();
  }

  private void sleep() {
    try {
      Thread.sleep(MessageSystem.DEFAULT_STEP_TIME_MS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void processQueue(final Addressee addressee, final ConcurrentLinkedQueue<MessageOld> queue) {
    while (!queue.isEmpty()) {
      MessageOld messageOld = queue.poll();
      addressee.accept(messageOld);
    }
  }

  @Override
  public void close() throws IOException {
    stop();
  }
}
