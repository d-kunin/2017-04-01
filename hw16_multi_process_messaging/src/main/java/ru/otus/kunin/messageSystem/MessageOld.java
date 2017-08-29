package ru.otus.kunin.messageSystem;

/**
 * @author tully
 */
public abstract class MessageOld {
  private final Address from;
  private final Address to;

  public MessageOld(Address from, Address to) {
    this.from = from;
    this.to = to;
  }

  public Address getFrom() {
    return from;
  }

  public Address getTo() {
    return to;
  }

  public abstract void exec(Addressee addressee);
}
