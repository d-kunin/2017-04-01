package ru.otus.kunin.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.frontend.jsonrpc.JsonRequest;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebsocketConnectionFactory implements WebSocketCreator {

  private static final Logger LOG = LoggerFactory.getLogger(WebsocketConnectionFactory.class);

  private final CopyOnWriteArraySet<Session> activeSessions = new CopyOnWriteArraySet<>();

  private final MessageSystemContext messageSystemContext;

  public WebsocketConnectionFactory(final MessageSystemContext messageSystemContext) {
    this.messageSystemContext = messageSystemContext;
  }

  @WebSocket
  public class WebsocketConnection {

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
      LOG.info("Connecting {}", session);
      activeSessions.add(session);
      LOG.info("Number of active sessions {}", activeSessions.size());

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
      LOG.info("Closing {} {} {}", statusCode, reason, session);
      activeSessions.remove(session);
      LOG.info("Number of active sessions {}", activeSessions.size());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String text) {
      final JsonRequest jsonRequest = JsonRequest.fromJson(text);
      LOG.info("Message {}", jsonRequest);
      onRequest(jsonRequest, session);
    }

    public void onRequest(JsonRequest jsonRequest, Session session) {
      if ("add".equals(jsonRequest.method())) {
        final String key = jsonRequest.params().get("key").textValue();
        final String value = jsonRequest.params().get("value").textValue();
        final String requestId = jsonRequest.id();
        final AddToCacheMessage addToCacheMessage =
            new AddToCacheMessage(messageSystemContext, new Address(requestId),
                                  messageSystemContext.cacheAddress(),
                                  key,
                                  value);
        messageSystemContext.messageSystem().addAddressee(
            new AddressableJsonRequest(jsonRequest, session, messageSystemContext));
        messageSystemContext.messageSystem().sendMessage(addToCacheMessage);
      }

      if ("get".equals(jsonRequest.method())) {

      }
    }
  }

  @Override
  public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
    LOG.info("Creating socket {}", req);
    if (req.hasSubProtocol("dima_v1")) {
      resp.setAcceptedSubProtocol("dima_v1");
      return new WebsocketConnection();
    } else {
      return null;
    }
  }

}
