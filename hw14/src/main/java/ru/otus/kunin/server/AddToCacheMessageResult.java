package ru.otus.kunin.server;

import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Message;

public class AddToCacheMessageResult extends Message {

  private final String result;
  private final String error;

  public AddToCacheMessageResult(final Address from,
                                 final Address to,
                                 final String result,
                                 final String error) {
    super(from, to);
    this.result = result;
    this.error = error;
  }

  @Override
  public void exec(final AddressableJsonRequest addressableJsonRequest) {
    addressableJsonRequest.sendResponse(result, error);
  }
}
