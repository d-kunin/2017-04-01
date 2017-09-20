package net.kundzi.messagesystem.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import net.kundzi.messagesystem.MessageSystemContext;
import net.kundzi.messagesystem.protocol.Address;

public class MessageSystemContextArgs {

  @Parameter(names = {"--serverHost", "-s"})
  public String serverHostname = "localhost";

  @Parameter(names = {"--serverPort", "-p"})
  public int serverPort = 9100;

  @Parameter(names = {"--frontend", "-f"})
  public String frontend;

  @Parameter(names = {"--backend", "-b"})
  public String backend;

  public MessageSystemContext toContext() {
    return MessageSystemContext.builder()
        .serverHostname(serverHostname)
        .serverPort(serverPort)
        .frontendAddress(Address.create(frontend))
        .backendAddress(Address.create(backend))
        .build();
  }

  public static MessageSystemContextArgs parse(String ... argv) {
    final MessageSystemContextArgs contextArgs = new MessageSystemContextArgs();
    JCommander.newBuilder()
        .addObject(contextArgs)
        .build()
        .parse(argv);
    return contextArgs;
  }

}
