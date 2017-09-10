package ru.otus.kunin.message2;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import ru.otus.kunin.messageSystem.Address;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MessageV2Test {

  @Test
  public void testSerializationOfEmptyPayloadObject() throws Exception {
    final MessageV2 original = MessageV2.create("1", null, "type_1", Address.create("_from"), Address.create("_to"), null);
    final byte[] jsonBytes = original.data();
    final MessageV2 restored = MessageV2.fromJsonBytes(jsonBytes).get();

    assertEquals(original.id(), restored.id());
    assertEquals(original.inResponseTo(), restored.inResponseTo());
    assertEquals(original.to(), restored.to());
    assertEquals(original.from(), restored.from());
    assertEquals(original.type(), restored.type());

    assertEquals(original.length(), restored.length());
    assertArrayEquals(original.data(), restored.data());
  }

  @Test
  public void testSerializationOfObjectWithPayload() throws Exception {
    final Map.Entry<String, ArrayList<String>> pojo = Maps.immutableEntry("key", Lists.newArrayList("value_list"));
    final JsonNode jsonNode = MessageV2.OBJECT_MAPPER.valueToTree(pojo);
    final MessageV2 original = MessageV2.create("1", "2", "type_1", Address.create("_from"), Address.create("_to"), jsonNode);
    final byte[] jsonBytes = original.data();
    final MessageV2 restored = MessageV2.fromJsonBytes(jsonBytes).get();

    assertEquals(original.id(), restored.id());
    assertEquals(original.inResponseTo(), restored.inResponseTo());
    assertEquals(original.to(), restored.to());
    assertEquals(original.from(), restored.from());
    assertEquals(original.type(), restored.type());
    assertEquals(original.payload(), restored.payload());

    assertEquals(original.length(), restored.length());
    assertArrayEquals(original.data(), restored.data());
  }
}