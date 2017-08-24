package ru.otus.kunin.app;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.app.message.AddToCacheMessage;
import ru.otus.kunin.app.message.GetFromCacheMessage;
import ru.otus.kunin.app.message.GetStatsMessage;
import ru.otus.kunin.jsonrpc.JsonRequest;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.Message;
import ru.otus.kunin.messageSystem.MessageSystemContext;

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
    if (jsonRequest != null) {
      onRequest(jsonRequest, session);
    } else {
      LOG.warn("Invalid client input: {}", text);
    }
  }

  public void onRequest(JsonRequest jsonRequest, Session session) {
    Message message = null;

    if ("add".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      final String value = jsonRequest.params().get("value").textValue();
      message = new AddToCacheMessage(messageSystemContext, Address.create(jsonRequest.id()),
                                      messageSystemContext.cacheAddress(),
                                      key,
                                      value);
    }

    if ("get".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      message = new GetFromCacheMessage(messageSystemContext,
                                        Address.create(jsonRequest.id()),
                                        messageSystemContext.cacheAddress(),
                                        key);
    }

    if ("stats".equals(jsonRequest.method())) {
      message = new GetStatsMessage(messageSystemContext,
                                    Address.create(jsonRequest.id()),
                                    messageSystemContext.cacheAddress());
    }

    if (message != null) {
      LOG.info("Routed request {}", jsonRequest);
      messageSystemContext.messageSystem().addAddressee(
          new AddressableJsonRequest(jsonRequest, session, messageSystemContext));
      messageSystemContext.messageSystem().sendMessage(message);
    }
  }
}
