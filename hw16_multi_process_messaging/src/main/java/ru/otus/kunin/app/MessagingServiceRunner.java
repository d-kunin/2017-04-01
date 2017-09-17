package ru.otus.kunin.app;

import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemServer;
import ru.otus.kunin.messageSystem.MessageSystemClient;

import java.net.InetSocketAddress;

public class MessagingServiceRunner {

  public static void main(String[] args) throws Exception {
//    final Server server = new Server(8090);
//    final AddressableCache addressableCache = new AddressableCache();
//
//    final ResourceHandler resourceHandler = new ResourceHandler();
//    resourceHandler.setDirectoriesListed(true);
//    resourceHandler.setBaseResource(Resource.newClassPathResource("./static/"));
//
    final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9100);
    final MessageSystemServer msgSystem = MessageSystemServer.create(serverAddress);
    msgSystem.start();

//    final MessageSystemContext messageSystemContext = MessageSystemContext.builder()
//        .messageSystem(messageSystem)
//        .cacheAddress(addressableCache.getAddress())
//        .build();
//    messageSystem.addAddressee(addressableCache);

//    final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//    final ServletHolder servletHolder = new ServletHolder(new WebsocketConnectorServlet(messageSystemContext));
//    servletContextHandler.addServlet(servletHolder, "/cache/websocket");
//    server.setHandler(new HandlerList(resourceHandler, servletContextHandler));
//    server.start();


    final MessageSystemClient clientFoo = MessageSystemClient.connect(serverAddress, Address.create("foo"));
    final MessageSystemClient clientBar = MessageSystemClient.connect(serverAddress, Address.create("bar"));

    clientFoo.setMessageListener((client, message) ->
                                     System.out.println("foo got " + message.topic() + " from " + message.from()));
    clientBar.setMessageListener((client, message) ->
                                     System.out.println("bar got " + message.topic() + " from " + message.from()));

    for (int i = 1; i < 1_000_000; i++) {
      System.out.println("step=" + i);
      clientFoo.send(MessageV2.createRequest(i + " love message to [bar]", Address.create("foo"), Address.create("bar"), null));
      clientBar.send(MessageV2.createRequest(i + " love message to [foo]", Address.create("bar"), Address.create("foo"), null));
    }

//    msgSystem.join();
    clientFoo.close();
    clientBar.close();
    msgSystem.close();

//    server.stop();
//    server.join();
    System.out.println("Done!");
  }

}
