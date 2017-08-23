package ru.otus.messageSystem;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MessageSystem implements Closeable {

  private static final long DEFAULT_STEP_TIME_MS = 10;
  private final ConcurrentHashMap<Address, ConcurrentLinkedQueue<Message>> messagesMap = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Address, Addressee> addresseeMap = new ConcurrentHashMap<>();
  private final AtomicBoolean isActive = new AtomicBoolean(true);

  public void addAddressee(Addressee addressee) {
    addresseeMap.put(addressee.getAddress(), addressee);
    messagesMap.put(addressee.getAddress(), new ConcurrentLinkedQueue<>());
  }

  public void removeAddressee(Addressee addressee) {
    addresseeMap.remove(addressee.getAddress());
    messagesMap.remove(addressee.getAddress());
  }

  public void sendMessage(Message message) {
    messagesMap.get(message.getTo()).add(message);
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
          final ConcurrentLinkedQueue<Message> queue = messagesMap.get(address);
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

  private void processQueue(final Addressee addressee, final ConcurrentLinkedQueue<Message> queue) {
    while (!queue.isEmpty()) {
      Message message = queue.poll();
      addressee.accept(message);
    }
  }

  @Override
  public void close() throws IOException {
    stop();
  }
}
