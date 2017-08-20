package ru.otus.kunin.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.kunin.dicache.DiCache;
import ru.otus.kunin.dorm.base.DormImpl;

public class ServerRunner {

  public static void main(String[] args) throws Exception {
    final Server server = new Server(8090);
    final ServletHandler servletHandler = new ServletHandler();
    server.setHandler(servletHandler);

    final CacheServlet cacheServlet = new CacheServlet(new DiCache<>(), DormImpl.create());
    servletHandler.addServletWithMapping(new ServletHolder(cacheServlet), "/cache");

    server.start();
    server.join();
  }

}
