package ru.otus.kunin.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.kunin.dicache.BackendComponent;
import ru.otus.kunin.front.WebsocketConnectorServlet;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemClient;
import ru.otus.kunin.messageSystem.MessageSystemContext;
import ru.otus.kunin.messageSystem.MessageSystemServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MessagingServiceRunner {

  public static void main(String[] args) throws Exception {

    final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9100);
    final MessageSystemContext messageSystemContext = MessageSystemContext.builder()
        .serverSocketAddress(serverAddress)
        .cacheAddress(Address.create("cache_1"))
        .frontendAddress(Address.create("front_1"))
        .build();

    final MessageSystemServer msgSystem = MessageSystemServer.create(serverAddress);
    msgSystem.start();

    final Server server = createFrontend(messageSystemContext);
    createBackend(messageSystemContext);


    server.join();
    msgSystem.join();

    System.out.println("Done!");
  }

  private static Server createFrontend(final MessageSystemContext messageSystemContext) throws Exception {
    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setBaseResource(Resource.newClassPathResource("./static/"));

    final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    final ServletHolder servletHolder = new ServletHolder(WebsocketConnectorServlet.create(messageSystemContext));
    servletContextHandler.addServlet(servletHolder, "/cache/websocket");
    final Server server = new Server(8090);
    server.setHandler(new HandlerList(resourceHandler,
                                      servletContextHandler));
    server.start();
    return server;
  }

  private static void createBackend(final MessageSystemContext messageSystemContext) throws IOException {
    final BackendComponent backendComponent = BackendComponent.create(messageSystemContext);
  }

  private static void experiment(final InetSocketAddress serverAddress) throws IOException {
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
  }

}
