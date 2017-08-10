package ru.otus.kunin.dorm.server;

import com.google.common.io.Closer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.kunin.dicache.base.DiCache;

import java.io.Closeable;
import java.io.IOException;

public class DormApplication implements Closeable {

  private final DiCache<String, String> cache;
  private final Closer closer = Closer.create();


  public static void main(String[] args) throws Exception {
    try (DormApplication dormApplication = new DormApplication()) {
      dormApplication.start();
    }
  }

  public DormApplication() {
    cache = closer.register(new DiCache<>());
  }

  void start() throws Exception {
    final ServletContextHandler context =
        new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

    context.setContextPath("/");
    context.addServlet(WelcomeServlet.class, "");
    context.addServlet(LoginServlet.class, "/login");
    context.addServlet(new ServletHolder(new CacheServlet(cache)), "/stats");
    context.setSecurityHandler(AuthUtil.createSecurityHandler());

    final Server server = new Server(8081);
    server.setHandler(context);
    server.start();
    server.join();
  }

  @Override
  public void close() throws IOException {
    closer.close();
  }
}
