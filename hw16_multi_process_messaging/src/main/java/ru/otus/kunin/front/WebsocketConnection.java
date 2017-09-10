package ru.otus.kunin.front;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message.AddToCacheMessageOld;
import ru.otus.kunin.message.GetFromCacheMessageOld;
import ru.otus.kunin.message.GetStatsMessageOld;
import ru.otus.kunin.front.jsonrpc.JsonRequest;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageOld;
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
    LOG.info("MessageOld {}", jsonRequest);
    if (jsonRequest != null) {
      onRequest(jsonRequest, session);
    } else {
      LOG.warn("Invalid client input: {}", text);
    }
  }

  public void onRequest(JsonRequest jsonRequest, Session session) {
    MessageOld messageOld = null;

    if ("add".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      final String value = jsonRequest.params().get("value").textValue();
      messageOld = new AddToCacheMessageOld(messageSystemContext, Address.create(jsonRequest.id()),
                                            messageSystemContext.cacheAddress(),
                                            key,
                                            value);
    }

    if ("get".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      messageOld = new GetFromCacheMessageOld(messageSystemContext,
                                              Address.create(jsonRequest.id()),
                                              messageSystemContext.cacheAddress(),
                                              key);
    }

    if ("stats".equals(jsonRequest.method())) {
      messageOld = new GetStatsMessageOld(messageSystemContext,
                                          Address.create(jsonRequest.id()),
                                          messageSystemContext.cacheAddress());
    }

    if (messageOld != null) {
      LOG.info("Routed request {}", jsonRequest);
//      messageSystemContext.messageSystem().addAddressee(
//          new AddressableJsonRequest(jsonRequest, session, messageSystemContext));
//      messageSystemContext.messageSystem().sendMessage(messageOld);
    }
  }
}
