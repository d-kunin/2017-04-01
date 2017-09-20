package net.kundzi.messagesystem.cli;

import net.kundzi.messagesystem.MessageSystemContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageSystemContextArgsTest {

  @Test
  public void testParse() throws Exception {
    String[] argv = {
        "-f", "front",
        "--backend", "backend",
        "-p", "666",
        "--serverHost", "kundzi.net"
    };
    final MessageSystemContext messageSystemContext = MessageSystemContextArgs.parse(argv).toContext();
    assertEquals("front", messageSystemContext.frontendAddress().id());
    assertEquals("backend", messageSystemContext.backendAddress().id());
    assertEquals("kundzi.net", messageSystemContext.serverHostname());
    assertEquals(666, messageSystemContext.serverPort());
  }

}