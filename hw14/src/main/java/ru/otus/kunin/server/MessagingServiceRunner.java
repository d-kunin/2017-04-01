package ru.otus.kunin.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import ru.otus.kunin.dicache.DiCache;
import ru.otus.kunin.dorm.base.DormImpl;

public class MessagingServiceRunner {

  public static void main(String[] args) throws Exception {
    final Server server = new Server(8090);
    final ServletHandler servletHandler = new ServletHandler();

    final CacheServlet cacheServlet = new CacheServlet(new DiCache<>(), DormImpl.create());
    servletHandler.addServletWithMapping(new ServletHolder(cacheServlet), "/cache");

    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setBaseResource(Resource.newClassPathResource("./static/"));

    server.setHandler(
        new HandlerList(resourceHandler,
                        servletHandler,
                        new DefaultHandler()));

    server.start();
    server.join();
  }

}
