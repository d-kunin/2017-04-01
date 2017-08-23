package ru.otus.kunin.app;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.jsonrpc.JsonRequest;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

import java.io.IOException;

@WebSocket
public class WebsocketConnection {

  private static final Logger LOG = LoggerFactory.getLogger(WebsocketConnection.class);

  private final MessageSystemContext messageSystemContext;

  public WebsocketConnection(final MessageSystemContext messageSystemContext) {
    this.messageSystemContext = messageSystemContext;
  }

  @OnWebSocketConnect
  public void onConnect(Session session) throws IOException {
    LOG.info("Connecting {}", session);

  }

  @OnWebSocketClose
  public void onClose(Session session, int statusCode, String reason) {
    LOG.info("Closing {} {} {}", statusCode, reason, session);
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
      final String key = jsonRequest.params().get("key").textValue();
      final String requestId = jsonRequest.id();
      final GetFromCacheMessage getFromCacheMessage =
          new GetFromCacheMessage(messageSystemContext,
                                  new Address(requestId),
                                  messageSystemContext.cacheAddress(),
                                  key);
      messageSystemContext.messageSystem().addAddressee(
          new AddressableJsonRequest(jsonRequest, session, messageSystemContext));
      messageSystemContext.messageSystem().sendMessage(getFromCacheMessage);
    }
  }
}
