package ru.otus.kunin.server;

import ru.otus.kunin.dicache.DiCache;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Addressee;
import ru.otus.messageSystem.Message;
import ru.otus.messageSystem.MessageSystemContext;

public class AddToCacheMessage extends Message {

  private final String key;
  private final String value;
  private final MessageSystemContext messageSystemContext;

  public AddToCacheMessage(final MessageSystemContext messageSystemContext,
                           final Address from,
                           final Address to,
                           final String key,
                           final String value) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
    this.key = key;
    this.value = value;
  }

  @Override
  public void exec(final Addressee addressee) {
    if (addressee instanceof DiCache) {
      execOnCache((DiCache<String, String>) addressee);
    }
  }

  private void execOnCache(DiCache<String, String> cache) {
    cache.put(key, value);
  }
}
