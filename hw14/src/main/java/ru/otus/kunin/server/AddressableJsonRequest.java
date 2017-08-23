package ru.otus.kunin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import ru.otus.kunin.frontend.jsonrpc.JsonRequest;
import ru.otus.kunin.frontend.jsonrpc.JsonResponse;
import ru.otus.messageSystem.Address;
import ru.otus.messageSystem.Addressee;
import ru.otus.messageSystem.Message;

import java.io.IOException;

public class AddressableJsonRequest implements Addressee {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final JsonRequest jsonRequest;
  private final Session session;

  public AddressableJsonRequest(final JsonRequest jsonRequest, final Session session) {
    this.jsonRequest = jsonRequest;
    this.session = session;
  }

  @Override
  public Address getAddress() {
    return new Address(jsonRequest.id());
  }

  @Override
  public void accept(final Message message) {
    message.exec(this);
  }

  public void sendResult(String result, String error) {
    final JsonResponse jsonResponse = JsonResponse.create(result, error, jsonRequest.id());
    try {
      final String json = OBJECT_MAPPER.writeValueAsString(jsonResponse);
      session.getRemote().sendString(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
