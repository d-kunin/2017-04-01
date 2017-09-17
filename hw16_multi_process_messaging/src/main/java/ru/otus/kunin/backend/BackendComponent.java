package ru.otus.kunin.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.kundzi.dicache.DiCache;
import ru.otus.kunin.message.AddToCacheMessage;
import ru.otus.kunin.message.GetFromCacheMessage;
import ru.otus.kunin.message.GetStatsMessage;
import net.kundzi.messagesystem.protocol.MessageV2;
import net.kundzi.messagesystem.MessageSystemClient;
import net.kundzi.messagesystem.MessageSystemContext;

import java.io.IOException;

public class BackendComponent implements MessageSystemClient.MessageListener {

  private static final Logger LOG = LoggerFactory.getLogger(BackendComponent.class);

  private final DiCache<String, String> cache;
  private final MessageSystemContext messageSystemContext;
  private final MessageSystemClient messageSystemClient;

  public static BackendComponent create(final MessageSystemContext messageSystemContext) throws IOException {
    final MessageSystemClient messageSystemClient = MessageSystemClient.connect(
        messageSystemContext.serverSocketAddress(),
        messageSystemContext.cacheAddress());
    return new BackendComponent(new DiCache<>(), messageSystemContext, messageSystemClient);
  }

  private BackendComponent(final DiCache<String, String> cache, final MessageSystemContext messageSystemContext, final MessageSystemClient messageSystemClient) {
    this.cache = cache;
    this.messageSystemContext = messageSystemContext;
    this.messageSystemClient = messageSystemClient;
    this.messageSystemClient.setMessageListener(this);
  }

  @Override
  public void onNewMessage(final MessageSystemClient client, final MessageV2 message) {
    LOG.info("Backend got message {}", message);

    final String topic = message.topic();
    switch (topic) {
      case AddToCacheMessage.TOPIC_ADD_TO_CACHE:
        onAddToCacheMessage(message);
        break;
      case GetFromCacheMessage.TOPIC_GET_FROM_CACHE:
        onGetFromCacheMessage(message);
        break;
      case GetStatsMessage.TOPIC_GET_STATS:
        onGetStatsMessage(message);
        break;
      default:
        LOG.warn("Unhandled message {}", message);
    }
  }

  private void onGetStatsMessage(final MessageV2 message) {
    final MessageV2 response = GetStatsMessage.createResponse(message, cache.getStats());
    messageSystemClient.send(response);
  }

  private void onGetFromCacheMessage(final MessageV2 message) {
    final GetFromCacheMessage.PayloadRequest addToCache = message.typedPayload(GetFromCacheMessage.PayloadRequest.class);
    final String key = addToCache.key();
    final String value = cache.get(key);
    final MessageV2 response = GetFromCacheMessage.createResponse(message, key, value);
    messageSystemClient.send(response);
  }

  private void onAddToCacheMessage(final MessageV2 message) {
    final AddToCacheMessage.PayloadRequest addToCache = message.typedPayload(AddToCacheMessage.PayloadRequest.class);
    final String key = addToCache.key();
    final String value = addToCache.value();
    cache.put(key, value);
    final MessageV2 response = AddToCacheMessage.createResponse(message, key, value);
    messageSystemClient.send(response);
  }
}
