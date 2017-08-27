package ru.otus.kunin.front;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import ru.otus.kunin.front.jsonrpc.JsonRequest;
import ru.otus.kunin.front.jsonrpc.JsonResponse;
import ru.otus.kunin.messageSystem.Address;
import ru.otus.kunin.messageSystem.Addressee;
import ru.otus.kunin.messageSystem.Message;
import ru.otus.kunin.messageSystem.MessageSystemContext;

import java.io.IOException;

public class AddressableJsonRequest implements Addressee {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final JsonRequest jsonRequest;
  private final Session session;
  private final MessageSystemContext messageSystemContext;


  public AddressableJsonRequest(final JsonRequest jsonRequest,
                                final Session session,
                                final MessageSystemContext messageSystemContext) {
    this.jsonRequest = jsonRequest;
    this.session = session;
    this.messageSystemContext = messageSystemContext;
  }

  @Override
  public Address getAddress() {
    return Address.create(jsonRequest.id());
  }

  @Override
  public void accept(final Message message) {
    message.exec(this);
  }

  public void sendResponse(Object result, String error) {
    final JsonResponse jsonResponse = JsonResponse.create(result, error, jsonRequest.id());
    try {
      final String json = OBJECT_MAPPER.writeValueAsString(jsonResponse);
      session.getRemote().sendString(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      messageSystemContext.messageSystem().removeAddressee(this);
    }
  }
}
