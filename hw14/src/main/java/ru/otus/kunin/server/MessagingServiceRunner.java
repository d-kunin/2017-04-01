package ru.otus.kunin.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.kunin.dicache.DiCache;
import ru.otus.kunin.dorm.base.DormImpl;

public class MessagingServiceRunner {

  public static void main(String[] args) throws Exception {
    final Server server = new Server(8090);

    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setBaseResource(Resource.newClassPathResource("./static/"));

    final CacheServlet cacheServlet = new CacheServlet(new DiCache<>(), DormImpl.create());
    final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    servletContextHandler.addServlet(new ServletHolder(cacheServlet), "/cache");
    servletContextHandler.addServlet(new ServletHolder(new WsCacheServlet()), "/cache/websocket");

    server.setHandler(
        new HandlerList(resourceHandler,
                        servletContextHandler,
                        new DefaultHandler()));

    server.start();
    server.join();
  }

}
