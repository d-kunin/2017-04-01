package ru.otus.kunin.app;

import ru.otus.messageSystem.AcyclicVisitorMessage;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.MessageSystemContext;

public class AddToCacheMessage extends AcyclicVisitorMessage<AddressableCache> {

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
  protected void visit(AddressableCache addressableCache) {
    addressableCache.put(key, value);
    messageSystemContext.messageSystem().sendMessage(
        new ResultMessage(getTo(), getFrom(), String.format("added {%s:%s}", key, value), null));
  }

}
