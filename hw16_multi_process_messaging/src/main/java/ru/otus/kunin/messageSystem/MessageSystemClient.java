package ru.otus.kunin.messageSystem;

import com.google.common.base.Preconditions;
import net.kundzi.socket.channels.client.NonBlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message2.MessageTopic;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.message2.MessageV2Reader;
import ru.otus.kunin.message2.MessageV2Writer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MessageSystemClient implements NonBlockingClient.IncomingMessageHandler<MessageV2>, Closeable {

  public interface MessageListener {
    void onNewMessage(MessageSystemClient client, MessageV2 message);
  }

  private static final Logger LOG = LoggerFactory.getLogger(MessageSystemContext.class);

  private final NonBlockingClient<MessageV2> client;
  private final AtomicBoolean isActive = new AtomicBoolean(false);
  private final Address address;
  private final AtomicReference<MessageListener> messageListenerRef = new AtomicReference<>();

  public static MessageSystemClient connect(InetSocketAddress serverAddress, Address thisClientAddress) throws IOException {
    Objects.nonNull(thisClientAddress);
    final NonBlockingClient<MessageV2> nonBlockingClient = NonBlockingClient.open(
        serverAddress,
        new MessageV2Reader(),
        new MessageV2Writer());

    final MessageSystemClient messageSystemClient = new MessageSystemClient(nonBlockingClient, thisClientAddress);
    messageSystemClient.isActive.set(true);
    final MessageV2 registerMessage = MessageV2.createRequest(MessageTopic.TYPE_REGISTER, thisClientAddress, Address.UPSTREAM, null);
    messageSystemClient.client.send(registerMessage);
    return messageSystemClient;
  }

  private MessageSystemClient(final NonBlockingClient<MessageV2> client, final Address address) {
    this.client = client;
    this.address = address;
    client.setIncomingMessageListener(this);
  }

  public void setMessageListener(MessageListener messageListener) {
    messageListenerRef.set(messageListener);
  }

  void stop() {
    isActive.set(false);
  }

  public void send(MessageV2 messageV2) {
    Preconditions.checkArgument(messageV2.from().equals(address), messageV2.from() + "!=" + address);
    client.send(messageV2);
  }

  @Override
  public void handle(final NonBlockingClient client, final MessageV2 message) {
    if (!isActive.get()) {
      LOG.info("Client ignore the message");
      return;
    }
    final MessageListener messageListener = messageListenerRef.get();
    messageListener.onNewMessage(this, message);
  }

  @Override
  public void close() throws IOException {
    stop();
    client.close();
  }
}
