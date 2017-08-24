package ru.otus.kunin.app.message;

import ru.otus.kunin.app.AddressableCache;
import ru.otus.kunin.messageSystem.AcyclicVisitorMessage;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemContext;

import javax.cache.management.CacheStatisticsMXBean;

public class GetStatsMessage extends AcyclicVisitorMessage<AddressableCache> {

  private final MessageSystemContext messageSystemContext;

  public GetStatsMessage(final MessageSystemContext messageSystemContext,
                         final Address from,
                         final Address to) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  protected void visit(AddressableCache addressableCache) {
    final CacheStatisticsMXBean stats = addressableCache.getStats();
    messageSystemContext.messageSystem().sendMessage(
        new JsonResponseMessage(getTo(), getFrom(), stats, null));
  }

}
