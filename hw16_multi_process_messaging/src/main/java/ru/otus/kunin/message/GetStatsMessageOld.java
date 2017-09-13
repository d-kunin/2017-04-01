package ru.otus.kunin.message;

import ru.otus.kunin.dicache.AddressableCache;
import ru.otus.kunin.messageSystem.AcyclicVisitorMessageOld;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemContext;

public class GetStatsMessageOld extends AcyclicVisitorMessageOld<AddressableCache> {

  private final MessageSystemContext messageSystemContext;

  public GetStatsMessageOld(final MessageSystemContext messageSystemContext,
                            final Address from,
                            final Address to) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  protected void visit(AddressableCache addressableCache) {
//    final CacheStatisticsMXBean stats = addressableCache.getStats();
//    messageSystemContext.messageSystem().sendMessage(
//        new JsonResponseMessageOld(getTo(), getFrom(), stats, null));
  }

}
