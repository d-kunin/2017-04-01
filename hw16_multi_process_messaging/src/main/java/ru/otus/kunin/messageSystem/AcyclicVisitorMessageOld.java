package ru.otus.kunin.messageSystem;

public abstract class AcyclicVisitorMessageOld<T extends Addressee> extends MessageOld {

  protected AcyclicVisitorMessageOld(Address from, Address to) {
    super(from, to);
  }

  final public void exec(Addressee addressee) {
    visit((T) addressee);
  }

  protected abstract void visit(T addressee);
}
