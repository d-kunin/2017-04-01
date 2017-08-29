package ru.otus.kunin.dicache;

import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.Addressee;
import ru.otus.kunin.messageSystem.MessageOld;

public class AddressableCache extends DiCache<String, String> implements Addressee {

  private final static Address ADDRESS = Address.create(AddressableCache.class.getSimpleName());

  @Override
  public Address getAddress() {
    return ADDRESS;
  }

  @Override
  public void accept(final MessageOld messageOld) {
    messageOld.exec(this);
  }

}
