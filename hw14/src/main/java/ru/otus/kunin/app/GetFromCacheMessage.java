package ru.otus.kunin.app;

import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Message;
import ru.otus.messageSystem.MessageSystemContext;

public class GetFromCacheMessage extends Message {

  private final String key;
  private final MessageSystemContext messageSystemContext;

  public GetFromCacheMessage(final MessageSystemContext messageSystemContext,
                             final Address from,
                             final Address to,
                             final String key) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
    this.key = key;
  }

  @Override
  public void exec(AddressableCache addressableCache) {
    final String value = addressableCache.get(key);
    messageSystemContext.messageSystem().sendMessage(
        new ResultMessage(getTo(), getFrom(), value, null));
  }

}
