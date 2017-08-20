package ru.otus.kunin.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WsCacheServlet extends WebSocketServlet {

  private static final Logger LOG = LoggerFactory.getLogger(WsCacheServlet.class);

  private final static long IDLE_MS = TimeUnit.HOURS.toMillis(36);

  @Override
  public void configure(final WebSocketServletFactory factory) {
    LOG.info("Configuring ... " + factory);
    factory.getPolicy().setIdleTimeout(IDLE_MS);
    factory.setCreator(new FrontEndSocketFactory());
    LOG.info("Configuration done");
  }

}
