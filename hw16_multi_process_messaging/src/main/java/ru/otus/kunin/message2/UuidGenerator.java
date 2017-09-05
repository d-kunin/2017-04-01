package ru.otus.kunin.message2;

import java.util.UUID;

public class UuidGenerator {

  private UuidGenerator() {}

  public static String nextUuid() {
    return UUID.randomUUID().toString();
  }
}
