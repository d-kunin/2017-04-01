package net.kundzi.socket.channels.server;

import net.kundzi.socket.channels.message.Message;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection<M extends Message> {

  private final SimpleReactorServer<M> simpleReactorServer;
  private final SocketChannel socketChannel;
  private SelectionKey key;
  private AtomicBoolean isMarkedDead = new AtomicBoolean(false);

  ClientConnection(final SimpleReactorServer<M> simpleReactorServer, final SocketChannel socketChannel) {
    this.simpleReactorServer = simpleReactorServer;
    this.socketChannel = Objects.requireNonNull(socketChannel);
  }

  public void send(M message) {
    simpleReactorServer.sendToClient(this, message);
  }

  public SocketAddress getRemoteAddress() {
    try {
      return socketChannel.getRemoteAddress();
    } catch (IOException e) {
      return null;
    }
  }

  SocketChannel getSocketChannel() {
    return socketChannel;
  }

  void register(Selector selector) throws ClosedChannelException {
    if (key != null) {
      throw new IllegalStateException();
    }
    key = getSocketChannel().register(selector, SelectionKey.OP_READ, this);
  }

  void unregister() {
    if (key.isValid()) {
      key.cancel();
    }
  }

  boolean isMarkedDead() {
    return isMarkedDead.get();
  }

  void markDead() {
    isMarkedDead.set(true);
  }
}
