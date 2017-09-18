package net.kundzi.nbserver.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.kundzi.nbserver.message.MessageReader;
import net.kundzi.nbserver.message.Message;
import net.kundzi.nbserver.message.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static net.kundzi.nbserver.util.Io.closeExecutorService;

public class NonBlockingClient<M extends Message> implements Closeable {

  private final static Logger LOG = LoggerFactory.getLogger(NonBlockingClient.class);

  @FunctionalInterface
  public interface IncomingMessageHandler<M extends Message> {
    void handle(NonBlockingClient client, M message);
  }

  public static <M extends Message> NonBlockingClient<M> open(SocketAddress serverAddress,
                                                              MessageReader<M> messageReader,
                                                              MessageWriter<M> messageWriter) throws IOException {
    final SocketChannel socketChannel = SocketChannel.open(serverAddress);
    socketChannel.configureBlocking(false);
    final Selector selector = Selector.open();
    socketChannel.register(selector, SelectionKey.OP_READ);
    return new NonBlockingClient<>(messageReader,
                                   messageWriter,
                                   selector,
                                   socketChannel);
  }

  private final MessageReader<M> messageReader;
  private final MessageWriter<M> messageWriter;
  private final Selector selector;
  private final SocketChannel socketChannel;

  private final ExecutorService selectExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("client-select-read-%d")
          .build()
  );

  private final BlockingDeque<M> outMessages = new LinkedBlockingDeque<>();
  private final ExecutorService outMessagesExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("client-out-messages-%d")
          .build()
  );

  private final BlockingDeque<M> inMessages = new LinkedBlockingDeque<>();
  private final ExecutorService inMessagesExecutor = Executors.newSingleThreadExecutor(
      new ThreadFactoryBuilder()
          .setDaemon(true)
          .setNameFormat("client-in-messages-%d")
          .build()
  );

  private final AtomicBoolean isActive = new AtomicBoolean(true);
  private final AtomicReference<IncomingMessageHandler<M>> incomingMessageListener = new AtomicReference<>();

  private NonBlockingClient(final MessageReader<M> messageReader,
                            final MessageWriter<M> messageWriter,
                            final Selector selector,
                            final SocketChannel socketChannel) {
    this.messageReader = messageReader;
    this.messageWriter = messageWriter;
    this.selector = selector;
    this.socketChannel = socketChannel;
    this.selectExecutor.execute(this::readSelectLoop);
    this.outMessagesExecutor.execute(this::outMessagesLoop);
    this.inMessagesExecutor.execute(this::inMessagesLoop);
  }


  public void send(M message) {
    outMessages.addLast(message);
  }

  public void setIncomingMessageListener(IncomingMessageHandler<M> incomingMessageHandler) {
    this.incomingMessageListener.set(incomingMessageHandler);
  }

  private void readSelectLoop() {
    while (isActive.get()) {
      try {
        final int numSelected = selector.select(1000); // TODO const
        if (0 == numSelected) {
          continue;
        }
        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (isActive.get() && iterator.hasNext()) {
          final SelectionKey key = iterator.next();
          LOG.info("Key {}", key.readyOps());
          final ArrayList<M> newMessages = new ArrayList<>(numSelected);
          try {
            if (key.isReadable()) {
              final Optional<M> newMessage = onReading();
              newMessage.ifPresent(newMessages::add);
            }
          } finally {
            iterator.remove();
          }
          newMessages.forEach(inMessages::addLast);
        }

      } catch (IOException e) {
        LOG.error("read select error", e);
      }
    }
  }

  private void outMessagesLoop() {
    try {
      for (; isActive.get(); ) {
        final M msg = outMessages.takeFirst();
        if (!socketChannel.finishConnect()) {
          LOG.info("Not ready, putting message back");
          outMessages.addFirst(msg);
        }
        messageWriter.write(socketChannel, msg);
      }
    } catch (InterruptedException e) {
      LOG.error("Interrupted", e);
    } catch (IOException e) {
      LOG.error("IO error", e);
    }
  }

  private void inMessagesLoop() {
    for (; isActive.get(); ) {
      try {
        final M msg = inMessages.takeFirst();
        final IncomingMessageHandler<M> handler = incomingMessageListener.get();
        if (null == handler) {
          LOG.info("Handler null, ignoring message TODO put back?");
          continue;
        }
        handler.handle(this, msg);
      } catch (InterruptedException e) {
        LOG.error("Error in IN message loop", e);
      }
    }
  }

  private Optional<M> onReading() {
    try {
      final M newMessage = messageReader.read(socketChannel);
      return Optional.of(newMessage);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  @Override
  public void close() throws IOException {
    LOG.info("Client is shutting down");
    isActive.set(false);
    closeExecutorService(selectExecutor);
    closeExecutorService(inMessagesExecutor);
    closeExecutorService(outMessagesExecutor);
    selector.close();
    socketChannel.close();
    LOG.info("Client is shut down");
  }

}
