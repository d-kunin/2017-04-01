package ru.otus.kunin.server;

import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Message;

public class ResultMessage extends Message {

  private final Object result;
  private final String error;

  public ResultMessage(final Address from,
                       final Address to,
                       final Object result,
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
