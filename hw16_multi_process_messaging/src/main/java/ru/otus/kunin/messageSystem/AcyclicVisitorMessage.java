package ru.otus.kunin.messageSystem;

public abstract class AcyclicVisitorMessage<T extends Addressee> extends Message {

  protected AcyclicVisitorMessage(Address from, Address to) {
    super(from, to);
  }

  final public void exec(Addressee addressee) {
    visit((T) addressee);
  }

  protected abstract void visit(T addressee);
}
