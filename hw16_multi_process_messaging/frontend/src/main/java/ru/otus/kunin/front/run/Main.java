package ru.otus.kunin.front.run;

import net.kundzi.messagesystem.MessageSystemContext;
import net.kundzi.messagesystem.cli.MessageSystemContextArgs;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.front.WebsocketConnectorServlet;

public class Main {

  public static void main(String[] args) throws Exception {
    final MessageSystemContext messageSystemContext = MessageSystemContextArgs.parse(args).toContext();

    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setResourceBase(Main.class.getClassLoader().getResource("static/").toExternalForm());

    final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    final ServletHolder servletHolder = new ServletHolder(WebsocketConnectorServlet.create(messageSystemContext));
    servletContextHandler.addServlet(servletHolder, "/cache/websocket");

    // NOTE: for demo purposes, port will be 8090 + <front_number> from frontend address
    final int portSerialNumber = Integer.parseInt(messageSystemContext.frontendAddress().id().split("_")[1]);
    final int httpPort = 8090 + portSerialNumber;
    final Server server = new Server(httpPort);
    server.setHandler(new HandlerList(resourceHandler, servletContextHandler));

    LoggerFactory.getLogger(Main.class).info("Starting Jetty on port {}", httpPort);
    server.start();
    server.join();
  }
}
