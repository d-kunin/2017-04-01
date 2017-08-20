package ru.otus.l151.messageSystem;

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

    public void sendMessage(Message message) {
        messagesMap.get(message.getTo()).add(message);
    }

    public void stop() {
      isActive.set(false);
    }

    public void start() {
        for (Map.Entry<Address, Addressee> entry : addresseeMap.entrySet()) {
            new Thread(() -> {
                while (isActive.get()) {
                    ConcurrentLinkedQueue<Message> queue = messagesMap.get(entry.getKey());
                    while (!queue.isEmpty()) {
                        Message message = queue.poll();
                        message.exec(entry.getValue());
                    }
                    try {
                        Thread.sleep(MessageSystem.DEFAULT_STEP_TIME_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void close() throws IOException {
      stop();
    }
}
