package net.kundzi.messagesystem.protocol;

import java.util.UUID;

public class UuidGenerator {

  private UuidGenerator() {
  }

  public static String nextUuid() {
    return UUID.randomUUID().toString();
  }
}
