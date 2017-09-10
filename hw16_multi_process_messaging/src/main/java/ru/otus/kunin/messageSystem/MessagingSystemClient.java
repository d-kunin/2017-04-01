package ru.otus.kunin.messageSystem;

import net.kundzi.socket.channels.client.NonBlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message2.MessageTypes;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.message2.MessageV2Reader;
import ru.otus.kunin.message2.MessageV2Writer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessagingSystemClient implements NonBlockingClient.IncomingMessageHandler<MessageV2>, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(MessageSystemContext.class);

  private final NonBlockingClient<MessageV2> client;
  private final AtomicBoolean isActive = new AtomicBoolean(false);
  private final Address address;

  public static MessagingSystemClient start(InetSocketAddress serverAddress, Address thisClientAddress) throws IOException {
    Objects.nonNull(thisClientAddress);
    final NonBlockingClient<MessageV2> nonBlockingClient = NonBlockingClient.open(
        serverAddress,
        new MessageV2Reader(),
        new MessageV2Writer());
    return new MessagingSystemClient(nonBlockingClient, thisClientAddress);
  }

  private MessagingSystemClient(final NonBlockingClient<MessageV2> client, final Address address) {
    this.client = client;
    this.address = address;
    client.setIncomingMessageListener(this);
  }

  public void start() {
    isActive.set(true);
    // TODO do we care about the response?
    client.send(MessageV2.createRequest(MessageTypes.TYPE_REGISTER, address, Address.UPSTREAM, null));
  }

  void stop() {
    isActive.set(false);
  }

  @Override
  public void handle(final NonBlockingClient client, final MessageV2 message) {
    LOG.info("Client is getting a message {}", message);
    if (!isActive.get()) {
      LOG.info("Client ignore the message");
      return;
    }

  }

  @Override
  public void close() throws IOException {
    stop();
    client.close();
  }
}
