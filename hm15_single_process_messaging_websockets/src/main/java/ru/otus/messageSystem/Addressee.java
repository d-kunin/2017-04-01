package ru.otus.messageSystem;

/**
 * @author tully
 */
public interface Addressee {
    Address getAddress();

    void accept(Message message);
}
