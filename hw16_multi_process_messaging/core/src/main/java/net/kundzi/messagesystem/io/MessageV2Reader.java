package net.kundzi.messagesystem.io;

import net.kundzi.messagesystem.protocol.MessageV2;
import net.kundzi.nbserver.message.MessageReader;
import net.kundzi.nbserver.message.lvmessage.LvMessage;
import net.kundzi.nbserver.message.lvmessage.LvMessageReader;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

public class MessageV2Reader implements MessageReader<MessageV2> {

  private final LvMessageReader readerImpl = new LvMessageReader();

  @Override
  public MessageV2 read(final ReadableByteChannel readableByteChannel) throws IOException {
    final LvMessage lvMessage = readerImpl.read(readableByteChannel);
    final Optional<MessageV2> messageV2 = MessageV2.fromJsonBytes(lvMessage.data());
    return messageV2.orElse(null);
  }
}
