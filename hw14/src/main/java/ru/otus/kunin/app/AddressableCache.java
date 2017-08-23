package ru.otus.kunin.app;

import ru.otus.kunin.dicache.DiCache;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Addressee;
import ru.otus.messageSystem.Message;

public class AddressableCache extends DiCache<String, String> implements Addressee {

  private final static Address ADDRESS = new Address(AddressableCache.class.getSimpleName());

  @Override
  public Address getAddress() {
    return ADDRESS;
  }

  @Override
  public void accept(final Message message) {
    message.exec(this);
  }

}
