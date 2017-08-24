package ru.otus.kunin.app;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.messageSystem.MessageSystemContext;

import java.util.concurrent.TimeUnit;

public class WebsocketConnectorServlet extends WebSocketServlet {

  private static final Logger LOG = LoggerFactory.getLogger(WebsocketConnectorServlet.class);
  private final static long IDLE_MS = TimeUnit.HOURS.toMillis(72);

  private final MessageSystemContext messageSystemContext;

  public WebsocketConnectorServlet(final MessageSystemContext messageSystemContext) {
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  public void configure(final WebSocketServletFactory factory) {
    LOG.info("Configuring ... " + factory);
    factory.getPolicy().setIdleTimeout(IDLE_MS);
    factory.setCreator(this::createWebSocket);
    LOG.info("Configuration done");
  }

  public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
    return new WebsocketConnection(messageSystemContext);
  }

}
