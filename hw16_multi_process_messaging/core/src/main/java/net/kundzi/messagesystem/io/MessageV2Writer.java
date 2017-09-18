package net.kundzi.messagesystem.io;

import net.kundzi.nbserver.message.MessageWriter;
import net.kundzi.nbserver.message.lvmessage.LvMessageWriter;
import net.kundzi.messagesystem.protocol.MessageV2;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public class MessageV2Writer implements MessageWriter<MessageV2> {

  private final LvMessageWriter writerImpl = new LvMessageWriter();

  @Override
  public void write(final WritableByteChannel writableByteChannel, final MessageV2 message) throws IOException {
    writerImpl.write(writableByteChannel, message);
  }
}
