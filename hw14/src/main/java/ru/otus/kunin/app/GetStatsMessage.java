package ru.otus.kunin.app;

import ru.otus.messageSystem.AcyclicVisitorMessage;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

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
        new ResultMessage(getTo(), getFrom(), stats, null));
  }

}
