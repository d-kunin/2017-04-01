package ru.otus.kunin.app;

import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Message;
import ru.otus.messageSystem.MessageSystemContext;

import javax.cache.management.CacheStatisticsMXBean;

public class GetStatsMessage extends Message {

  private final MessageSystemContext messageSystemContext;

  public GetStatsMessage(final MessageSystemContext messageSystemContext,
                         final Address from,
                         final Address to) {
    super(from, to);
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  public void exec(AddressableCache addressableCache) {
    final CacheStatisticsMXBean stats = addressableCache.getStats();
    messageSystemContext.messageSystem().sendMessage(
        new ResultMessage(getTo(), getFrom(), stats, null));
  }

}
