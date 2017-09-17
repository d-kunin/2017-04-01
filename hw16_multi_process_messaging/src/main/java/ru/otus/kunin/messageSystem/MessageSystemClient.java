package ru.otus.kunin.messageSystem;

import com.google.common.base.Preconditions;
import net.kundzi.socket.channels.client.NonBlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message2.MessageTopic;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.message2.MessageV2Reader;
import ru.otus.kunin.message2.MessageV2Writer;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MessageSystemClient implements NonBlockingClient.IncomingMessageHandler<MessageV2>, Closeable {

  public interface MessageListener {
    void onNewMessage(MessageSystemClient client, MessageV2 message);
  }

  public interface ResponseCallback {
    void onResponse(MessageV2 response);
  }

  private static final Logger LOG = LoggerFactory.getLogger(MessageSystemContext.class);

  private final NonBlockingClient<MessageV2> client;
  private final AtomicBoolean isActive = new AtomicBoolean(false);
  private final Address address;
  private final AtomicReference<MessageListener> messageListenerRef = new AtomicReference<>();

  private final ConcurrentHashMap<String, ResponseCallback> requestIdToCallback = new ConcurrentHashMap<>();

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

  public void send(MessageV2 messageV2, @Nonnull ResponseCallback responseCallback) {
    Preconditions.checkArgument(messageV2.from().equals(address), messageV2.from() + "!=" + address);
    Preconditions.checkNotNull(responseCallback);
    requestIdToCallback.put(messageV2.id(), responseCallback);
    client.send(messageV2);
  }

  @Override
  public void handle(final NonBlockingClient client, final MessageV2 message) {
    if (!isActive.get()) {
      LOG.warn("Client ignore the message");
      return;
    }

    final String inResponseTo = message.inResponseTo();
    if (null != inResponseTo) {
      final ResponseCallback callback = requestIdToCallback.remove(inResponseTo);
      if (null != callback) {
        callback.onResponse(message);
      } else {
        LOG.error("Response with no callback to deliver to {}", message);
      }
    }

    final MessageListener messageListener = messageListenerRef.get();
    if (null != messageListener) {
      messageListener.onNewMessage(this, message);
    }
  }

  @Override
  public void close() throws IOException {
    stop();
    client.close();
  }
}
