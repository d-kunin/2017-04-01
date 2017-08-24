package ru.otus.kunin.app;

import ru.otus.messageSystem.AcyclicVisitorMessage;
import ru.otus.messageSystem.Address;

public class ResultMessage extends AcyclicVisitorMessage<AddressableJsonRequest> {

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
  protected void visit(final AddressableJsonRequest addressableJsonRequest) {
    addressableJsonRequest.sendResponse(result, error);
  }
}
