package net.kundzi.socket.channels.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.kundzi.socket.channels.IoUtil;
import net.kundzi.socket.channels.message.Message;
import net.kundzi.socket.channels.message.MessageReader;
import net.kundzi.socket.channels.message.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

public class SimpleReactorServer<M extends Message> implements Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleReactorServer.class);

  @FunctionalInterface
  public interface IncomingMessageHandler<M extends Message> {
    void handle(ClientConnection from, M message);
  }

  enum State {
    NOT_STARTED,
    STARTED,
    STOPPED
  }

  private static class MessageEvent<M> {
    final M message;
    final ClientConnection connection;

    MessageEvent(final ClientConnection connection, final M message) {
      this.message = message;
      this.connection = connection;
    }
  }

  public static <M extends Message> SimpleReactorServer<M> start(final InetSocketAddress bindAddress,
                                                                 final MessageReader<M> messageReader,
                                                                 final MessageWriter<M> messageWriter) throws IOException {

    final ServerSocketChannel socketChannel = ServerSocketChannel.open().bind(bindAddress);
    LOG.info("Listening to address {}", socketChannel.getLocalAddress());
    socketChannel.configureBlocking(false);
    final Selector selector = Selector.open();
    socketChannel.register(selector, SelectionKey.OP_ACCEPT, null);

    return new SimpleReactorServer<>(selector,
                                     socketChannel,
                                     messageReader,
                                     messageWriter);

  }

  public SimpleReactorServer(final Selector selector,
                             final ServerSocketChannel boundServerChannel,
                             final MessageReader<M> messageReader,
                             final MessageWriter<M> messageWriter) {
    this.selector = selector;
    this.boundServerChannel = boundServerChannel;
    this.messageReader = messageReader;
    this.messageWriter = messageWriter;

    state.set(State.STARTED);
    selectExecutor.execute(this::readAcceptSelectLoop);
    inMessagesExecutor.execute(this::inMessagesLoop);
    outMessagesExecutor.execute(this::outMessagesLoop);
    reaperExecutor.scheduleAtFixedRate(this::harvestDeadConnections, 100, 100, TimeUnit.MILLISECONDS);
  }

  private final Selector selector;
  private final ServerSocketChannel boundServerChannel;

  private final MessageReader<M> messageReader;
  private final MessageWriter<M> messageWriter;
  private final AtomicReference<IncomingMessageHandler<M>> incomingMessageHandlerRef = new AtomicReference<>();
  private final AtomicReference<State> state = new AtomicReference<>(State.NOT_STARTED);
  private final CopyOnWriteArrayList<ClientConnection> clients = new CopyOnWriteArrayList<>();

  private final ExecutorService selectExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("server-read-accept-select-%d")
          .build()
  );
  private final ScheduledExecutorService reaperExecutor =
      Executors.newScheduledThreadPool(1,
                                       new ThreadFactoryBuilder()
                                           .setDaemon(true)
                                           .setNameFormat("server-reaper-%d")
                                           .build());

  private final BlockingDeque<MessageEvent<M>> outMessages = new LinkedBlockingDeque<>();
  private final ExecutorService outMessagesExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("server-out-messages-%d")
          .build()
  );
  private final BlockingDeque<MessageEvent<M>> inMessages = new LinkedBlockingDeque<>();
  private final ExecutorService inMessagesExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("server-in-messages-%d")
          .build()
  );

  public void setIncomingMessageHandler(IncomingMessageHandler<M> messageHandler) {
    this.incomingMessageHandlerRef.set(messageHandler);
  }

  public void stop() {
    LOG.info("Stopping server");
    state.set(State.STOPPED);
    IoUtil.closeExecutorService(inMessagesExecutor);
    IoUtil.closeExecutorService(outMessagesExecutor);
    IoUtil.closeExecutorService(selectExecutor);
    IoUtil.closeExecutorService(reaperExecutor);
    try {
      selector.close();
    } catch (IOException e) {
      LOG.error("Error stopping", e);
    }
    getClients().forEach(client -> {
      try {
        LOG.info("server closing :" + client.getRemoteAddress());
        client.unregister();
        client.getSocketChannel().close();
        LOG.info("server closed :" + client.getRemoteAddress());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    LOG.info("Stopped server");
  }

  public void join() {
    if (state.get() != State.STARTED) {
      return;
    }
    try {
      selectExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
      reaperExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
      inMessagesExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
      outMessagesExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      LOG.error("Error joining {}", e);
    }
  }

  void sendToClient(final ClientConnection<M> clientConnection, final M message) {
    outMessages.addLast(new MessageEvent<>(clientConnection, message));
  }

  private List<ClientConnection> getClients() {
    return Collections.unmodifiableList(clients);
  }

  private void readAcceptSelectLoop() {
    try {
      _loop();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void _loop() throws IOException {
    while (isNotStopped()) {
      final int numSelected = selector.select();
      if (0 == numSelected) {
        continue;
      }

      final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (isNotStopped() && iterator.hasNext()) {
        final SelectionKey key = iterator.next();
        final ArrayList<MessageEvent<M>> newMessages = new ArrayList<>(numSelected);

        try {
          if (key.isAcceptable()) {
            final SocketChannel socketChannel = boundServerChannel.accept();
            final ClientConnection<M> newClient = new ClientConnection<>(this, socketChannel);
            onAccepting(newClient);
          }

          if (key.isReadable()) {
            final ClientConnection client = (ClientConnection) key.attachment();
            onReading(client).ifPresent(message -> newMessages.add(new MessageEvent<>(client, message)));
          }
          newMessages.forEach(inMessages::addLast);
        } catch (CancelledKeyException e) {
          e.printStackTrace();
          // carry on
        } finally {
          iterator.remove();
        }
      }
    }

  }

  private boolean isNotStopped() {
    return state.get() != State.STOPPED;
  }

  private void onAccepting(ClientConnection client) throws IOException {
    client.getSocketChannel().configureBlocking(false);
    client.register(selector);
    clients.add(client);
    LOG.info("New connection established {}", client.getRemoteAddress());
  }

  private Optional<M> onReading(ClientConnection client) {
    try {
      final M message = messageReader.read(client.getSocketChannel());
      return Optional.of(message);
    } catch (IOException e) {
      client.markDead();
      return Optional.empty();
    }
  }

  private void inMessagesLoop() {
    for (; isNotStopped(); ) {
      try {
        final MessageEvent<M> mEvent = inMessages.takeFirst();
        final IncomingMessageHandler<M> messageHandler = incomingMessageHandlerRef.get();
        if (null == messageHandler) {
          continue;
        }
        messageHandler.handle(mEvent.connection, mEvent.message);
      } catch (InterruptedException e) {
        LOG.error("Error in IN message readAcceptSelectLoop", e);
      }
    }
  }

  private void outMessagesLoop() {
    for (; isNotStopped(); ) {
      try {
        final MessageEvent<M> mEvent = outMessages.takeFirst();
        if (mEvent.connection.isMarkedDead()) {
          LOG.info("Dead client, skipping message");
          continue;
        }
        messageWriter.write(mEvent.connection.getSocketChannel(), mEvent.message);
      } catch (InterruptedException | IOException e) {
        LOG.error("Error in OUT message", e);
      }
    }
  }

  private void harvestDeadConnections() {
    if (isNotStopped()) {
      final List<ClientConnection> deadConnections = clients.stream()
          .filter(ClientConnection::isMarkedDead)
          .collect(toList());

      if (deadConnections.isEmpty()) {
        return;
      }

      LOG.info("Harvested clients: " + deadConnections.size());
      deadConnections.forEach(clientConnection -> {
        try {
          LOG.info("removing: " + clientConnection.getRemoteAddress());
          clientConnection.unregister();
          clientConnection.getSocketChannel().close();
        } catch (IOException e) {
        }
      });
      clients.removeAll(deadConnections);
    }
  }

  @Override
  public void close() throws IOException {
    stop();
  }

}
