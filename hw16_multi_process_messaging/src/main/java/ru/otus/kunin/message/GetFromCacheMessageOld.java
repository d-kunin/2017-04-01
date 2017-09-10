package ru.otus.kunin.message;

import ru.otus.kunin.dicache.AddressableCache;
import ru.otus.kunin.messageSystem.AcyclicVisitorMessageOld;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemContext;

public class GetFromCacheMessageOld extends AcyclicVisitorMessageOld<AddressableCache> {

  private final String key;
  private final MessageSystemContext messageSystemContext;

  public GetFromCacheMessageOld(final MessageSystemContext messageSystemContext,
                                final Address from,
                                final Address to,
                                final String key) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
    this.key = key;
  }

  @Override
  protected void visit(AddressableCache addressableCache) {
//    final String value = addressableCache.get(key);
//    messageSystemContext.messageSystem().sendMessage(
//        new JsonResponseMessageOld(getTo(), getFrom(), value, null));
  }

}
