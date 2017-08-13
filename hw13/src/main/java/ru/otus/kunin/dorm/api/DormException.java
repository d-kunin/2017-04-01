package ru.otus.kunin.dorm.api;

public class DormException extends RuntimeException {

  public DormException(String message) {
    super(message);
  }

  public DormException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
