package net.kundzi.socket.channels;

import net.kundzi.socket.channels.client.NonBlockingClient;
import net.kundzi.socket.channels.message.lvmessage.DefaultLvMessage;
import net.kundzi.socket.channels.message.lvmessage.LvMessage;
import net.kundzi.socket.channels.message.lvmessage.LvMessageReader;
import net.kundzi.socket.channels.message.lvmessage.LvMessageWriter;
import net.kundzi.socket.channels.server.SimpleReactorServer;
import net.kundzi.socket.channels.server.SimpleReactorServer.IncomingMessageHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import static java.lang.System.out;

public class Main {

  public static void main(String[] args) throws Exception {
    nonblockingServer();
  }

  private static void nonblockingServer() throws IOException, InterruptedException {
    final String host = "localhost";
    final int port = 6677;
    final InetSocketAddress serverSockAddress = new InetSocketAddress(host, port);

    final IncomingMessageHandler<LvMessage> handler = (from, message) -> {
      final String inMessage = new String(message.data());
      out.println(from.getRemoteAddress() + " " + message.length() + " " + inMessage);
      final String outMessage = inMessage + " pong";
      out.println("responding to " + from.getRemoteAddress());
      from.send(new DefaultLvMessage(outMessage.getBytes()));
    };

    final SimpleReactorServer<LvMessage> simpleReactorServer = SimpleReactorServer.start(
        serverSockAddress,
        new LvMessageReader(),
        new LvMessageWriter()
    );
    simpleReactorServer.setIncomingMessageHandler(handler);


    final ArrayList<Closeable> closeables = new ArrayList<>();

    for (int i = 0; i < 650; i++) {
      System.out.println("Step " + i);
      closeables.add(startClient(serverSockAddress));
    }

    Thread.sleep(10000);
    out.println("stopping ...");
    for (Closeable closeable : closeables) {
      closeable.close();
    }
    simpleReactorServer.stop();
    simpleReactorServer.join();
    out.println("stopped");
  }

  private static NonBlockingClient<LvMessage> startClient(final InetSocketAddress serverSockAddress) throws IOException {
    final NonBlockingClient<LvMessage> nonBlockingClient =
        NonBlockingClient.open(serverSockAddress, new LvMessageReader(), new LvMessageWriter());
    nonBlockingClient.setIncomingMessageListener((client, message) -> {
      final String respondWith = new String(message.data()) + " ping";
      try {
        Thread.sleep(30);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      client.send(new DefaultLvMessage(respondWith.getBytes()));
    });
    nonBlockingClient.send(new DefaultLvMessage("ping".getBytes()));
    return nonBlockingClient;
  }

}
