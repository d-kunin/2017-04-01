package ru.otus.kunin.front;

import ru.otus.kunin.front.jsonrpc.JsonRequest;
import ru.otus.kunin.front.jsonrpc.JsonResponse;
import net.kundzi.messagesystem.MessageSystemClient;
import net.kundzi.messagesystem.MessageSystemContext;
import net.kundzi.messagesystem.protocol.MessageV2;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.kunin.message.AddToCacheMessage;
import ru.otus.kunin.message.GetFromCacheMessage;
import ru.otus.kunin.message.GetStatsMessage;

import java.io.IOException;

@WebSocket
public class WebsocketConnection {

  private static final Logger LOG = LoggerFactory.getLogger(WebsocketConnection.class);

  private final MessageSystemContext messageSystemContext;
  private final MessageSystemClient messageSystemClient;

  public WebsocketConnection(final MessageSystemContext messageSystemContext,
                             final MessageSystemClient messageSystemClient) {
    this.messageSystemContext = messageSystemContext;
    this.messageSystemClient = messageSystemClient;
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
    LOG.info("JsonRequest {}", jsonRequest);
    if (jsonRequest != null) {
      onRequest(jsonRequest, session);
    } else {
      LOG.warn("Invalid client input: {}", text);
    }
  }

  public void onRequest(JsonRequest jsonRequest, Session session) {
    MessageV2 messageV2 = null;

    if ("add".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      final String value = jsonRequest.params().get("value").textValue();
      messageV2 = AddToCacheMessage.createRequest(messageSystemContext.frontendAddress(),
                                                  messageSystemContext.cacheAddress(),
                                                  key,
                                                  value);
    }

    if ("get".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      messageV2 = GetFromCacheMessage.createRequest(messageSystemContext.frontendAddress(),
                                                    messageSystemContext.cacheAddress(),
                                                    key);
    }

    if ("stats".equals(jsonRequest.method())) {
      messageV2 = GetStatsMessage.createRequest(messageSystemContext.frontendAddress(),
                                                messageSystemContext.cacheAddress());
    }

    if (messageV2 != null) {
      LOG.info("Routed request {}", jsonRequest);
      messageSystemClient.send(messageV2, response -> {
        try {
          final String json = JsonResponse.create(response.payload(), null, jsonRequest.id()).toJson();
          session.getRemote().sendString(json);
        } catch (IOException e) {
          LOG.error("Failed to send json response", e);
        }
      });
    }
  }
}
