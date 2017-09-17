package ru.otus.kunin.front;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message2.MessageV2;
import ru.otus.kunin.messageSystem.MessageSystemClient;
import ru.otus.kunin.messageSystem.MessageSystemContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WebsocketConnectorServlet extends WebSocketServlet implements MessageSystemClient.MessageListener {


  private static final Logger LOG = LoggerFactory.getLogger(WebsocketConnectorServlet.class);
  private static final long IDLE_MS = TimeUnit.HOURS.toMillis(72);
  private final MessageSystemContext messageSystemContext;

  public static WebsocketConnectorServlet create(final MessageSystemContext messageSystemContext) throws IOException {
    final MessageSystemClient messageSystemClient =
        MessageSystemClient.connect(messageSystemContext.serverSocketAddress(),
                                    messageSystemContext.frontendAddress());
    return new WebsocketConnectorServlet(messageSystemContext, messageSystemClient);
  }

  private final MessageSystemClient messageSystemClient;

  private WebsocketConnectorServlet(final MessageSystemContext messageSystemContext,
                                    final MessageSystemClient messageSystemClient) {
    this.messageSystemContext = messageSystemContext;
    this.messageSystemClient = messageSystemClient;
    messageSystemClient.setMessageListener(this);
  }

  @Override
  public void configure(final WebSocketServletFactory factory) {
    LOG.info("Configuring ... " + factory);
    factory.getPolicy().setIdleTimeout(IDLE_MS);
    factory.setCreator(this::createWebSocket);
    LOG.info("Configuration done");
  }

  public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
    return new WebsocketConnection(messageSystemContext, messageSystemClient);
  }

  @Override
  public void onNewMessage(final MessageSystemClient client, final MessageV2 message) {
    LOG.info("Servlet got message: {}", message);
  }
}
