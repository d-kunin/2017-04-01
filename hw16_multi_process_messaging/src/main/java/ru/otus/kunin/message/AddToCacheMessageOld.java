package ru.otus.kunin.message;

import ru.otus.kunin.dicache.AddressableCache;
import ru.otus.kunin.messageSystem.AcyclicVisitorMessageOld;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.MessageSystemContext;

public class AddToCacheMessageOld extends AcyclicVisitorMessageOld<AddressableCache> {

  private final String key;
  private final String value;
  private final MessageSystemContext messageSystemContext;

  public AddToCacheMessageOld(final MessageSystemContext messageSystemContext,
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
  protected void visit(AddressableCache addressableCache) {
//    addressableCache.put(key, value);
//    messageSystemContext.messageSystem().sendMessage(
//        new JsonResponseMessageOld(getTo(), getFrom(), String.format("added {%s:%s}", key, value), null));
  }

}
