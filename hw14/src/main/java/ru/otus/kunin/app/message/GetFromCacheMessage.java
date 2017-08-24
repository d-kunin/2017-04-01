package ru.otus.kunin.app.message;

import ru.otus.kunin.app.AddressableCache;
import ru.otus.messageSystem.AcyclicVisitorMessage;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

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
