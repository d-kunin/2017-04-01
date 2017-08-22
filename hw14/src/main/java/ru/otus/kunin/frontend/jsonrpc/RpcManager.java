package ru.otus.kunin.frontend.jsonrpc;

import ru.otus.kunin.server.AddToCacheMessage;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

public class RpcManager {

  private final MessageSystemContext messageSystemContext;

  public RpcManager(final MessageSystemContext messageSystemContext) {
    this.messageSystemContext = messageSystemContext;
  }

  public void onRequest(JsonRequest jsonRequest, String senderId) {
    if ("add".equals(jsonRequest.method())) {
      final String key = jsonRequest.params().get("key").textValue();
      final String value = jsonRequest.params().get("value").textValue();
      final AddToCacheMessage addToCacheMessage =
          new AddToCacheMessage(messageSystemContext, new Address(senderId),
                                messageSystemContext.cacheAddress(),
                                key,
                                value);
      messageSystemContext.messageSystem().sendMessage(addToCacheMessage);
    }

    if ("get".equals(jsonRequest.method())) {

    }
  }

}
