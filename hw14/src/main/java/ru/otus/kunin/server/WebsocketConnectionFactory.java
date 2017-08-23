package ru.otus.kunin.server;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import ru.otus.messageSystem.MessageSystemContext;

public class WebsocketConnectionFactory implements WebSocketCreator {

  private final MessageSystemContext messageSystemContext;

  public WebsocketConnectionFactory(final MessageSystemContext messageSystemContext) {
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
      return new WebsocketConnection(messageSystemContext);
    }
  }
