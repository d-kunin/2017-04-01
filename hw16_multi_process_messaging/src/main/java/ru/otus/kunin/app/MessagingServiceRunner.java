package ru.otus.kunin.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.kunin.dicache.AddressableCache;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystem;
import ru.otus.kunin.messageSystem.MessagingSystemClient;

import java.net.InetSocketAddress;

public class MessagingServiceRunner {

  public static void main(String[] args) throws Exception {
    final Server server = new Server(8090);
    final AddressableCache addressableCache = new AddressableCache();

    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setBaseResource(Resource.newClassPathResource("./static/"));

    final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9100);
    final MessageSystem msgSystem = MessageSystem.create(serverAddress);
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


    final MessagingSystemClient clientFoo = MessagingSystemClient.connect(serverAddress, Address.create("foo"));
    final MessagingSystemClient clientBar = MessagingSystemClient.connect(serverAddress, Address.create("bar"));

    clientFoo.setMessageListener((client, message) ->
                                     System.out.println("foo got " + message.type() + " from " + message.from()));
    clientBar.setMessageListener((client, message) ->
                                     System.out.println("bar got " + message.type() + " from " + message.from()));

    // TODO fix race condition, there must be one
    clientFoo.send(MessageV2.createRequest("love message to [bar]", Address.create("foo"), Address.create("bar"), null));
    clientBar.send(MessageV2.createRequest("love message to [foo]", Address.create("bar"), Address.create("foo"), null));

    server.start();
    server.join();
  }

}
