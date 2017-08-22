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
import ru.otus.kunin.frontend.jsonrpc.RpcManager;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

public class FrontEndSocketManager implements WebSocketCreator {

  private static final Logger LOG = LoggerFactory.getLogger(FrontEndSocketManager.class);

  private final CopyOnWriteArraySet<Session> activeSessions = new CopyOnWriteArraySet<>();

  private final RpcManager rpcManager;

  public FrontEndSocketManager(final RpcManager rpcManager) {
    this.rpcManager = rpcManager;
  }

  @WebSocket
  public class FrontEndSocket {

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
      rpcManager.onRequest(jsonRequest, session.getRemote().getInetSocketAddress().toString());
    }
  }

  @Override
  public Object createWebSocket(final ServletUpgradeRequest req, final ServletUpgradeResponse resp) {
    LOG.info("Creating socket {}", req);
    if (req.hasSubProtocol("dima_v1")) {
      resp.setAcceptedSubProtocol("dima_v1");
      return new FrontEndSocket();
    } else {
      return null;
    }
  }

}
