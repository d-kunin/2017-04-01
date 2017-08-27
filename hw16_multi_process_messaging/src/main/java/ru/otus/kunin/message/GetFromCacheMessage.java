package ru.otus.kunin.message;

import ru.otus.kunin.dicache.AddressableCache;
import ru.otus.kunin.messageSystem.AcyclicVisitorMessage;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemContext;

public class GetFromCacheMessage extends AcyclicVisitorMessage<AddressableCache> {

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
  protected void visit(AddressableCache addressableCache) {
    final String value = addressableCache.get(key);
    messageSystemContext.messageSystem().sendMessage(
        new JsonResponseMessage(getTo(), getFrom(), value, null));
  }

}
