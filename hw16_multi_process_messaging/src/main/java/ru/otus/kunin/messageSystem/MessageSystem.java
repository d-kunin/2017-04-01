package ru.otus.kunin.messageSystem;

import net.kundzi.socket.channels.server.ClientConnection;
import net.kundzi.socket.channels.server.SimpleReactorServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.message2.MessageV2Reader;
import ru.otus.kunin.message2.MessageV2Writer;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MessageSystem implements Closeable, SimpleReactorServer.IncomingMessageHandler<MessageV2> {

  private static final Logger LOG = LoggerFactory.getLogger(MessageSystem.class);

  private final ConcurrentHashMap<Address, ClientConnection<MessageV2>> addresseeMap = new ConcurrentHashMap<>();
  private final AtomicBoolean isActive = new AtomicBoolean(true);
  private final SimpleReactorServer<MessageV2> server;

  public static MessageSystem create(InetSocketAddress bindAddress) throws IOException {
    final SimpleReactorServer<MessageV2> server =
        SimpleReactorServer.start(bindAddress, new MessageV2Reader(), new MessageV2Writer());
    return new MessageSystem(server);
  }

  private MessageSystem(final SimpleReactorServer<MessageV2> server) {
    this.server = server;
    server.setIncomingMessageHandler(this);
  }

  public void addAddressee(ClientConnection<MessageV2> connection, Address address) {
    addresseeMap.put(address, connection);
  }

  public void removeAddressee(Address address) {
    addresseeMap.remove(address);
  }

  public void sendMessage(MessageV2 messageV2, @Nullable Address address) {
    // TODO send to everyone or a single connection
  }

  public void stop() {
    isActive.set(false);
    server.stop();
  }

  public void start() {
    isActive.set(true);
  }

  @Override
  public void close() throws IOException {
    stop();
  }

  @Override
  public void handle(final ClientConnection from, final MessageV2 message) {
    if (!isActive.get()) {
      LOG.info("Ignoring, not active: " + message.toString());
      return;
    }
    // TODO do handle
  }
}
