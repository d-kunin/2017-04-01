package ru.otus.kunin.backend.run;

import net.kundzi.messagesystem.MessageSystemContext;
import net.kundzi.messagesystem.cli.MessageSystemContextArgs;
import ru.otus.kunin.backend.BackendComponent;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws InterruptedException, IOException {
    final MessageSystemContext messageSystemContext = MessageSystemContextArgs.parse(args).toContext();
    final BackendComponent backendComponent = BackendComponent.create(messageSystemContext);

    // Just a hack to
    Thread.currentThread().join();
  }

}
