package ru.otus.kunin.message2;

import net.kundzi.socket.channels.message.MessageReader;
import net.kundzi.socket.channels.message.MessageWriter;
import net.kundzi.socket.channels.message.lvmessage.LvMessageWriter;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class MessageV2Writer implements MessageWriter<MessageV2> {

  private final LvMessageWriter writerImpl = new LvMessageWriter();

  @Override
  public void write(final WritableByteChannel writableByteChannel, final MessageV2 message) throws IOException {
    writerImpl.write(writableByteChannel, message);
  }
}
