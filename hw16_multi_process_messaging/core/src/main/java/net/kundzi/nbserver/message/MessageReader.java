package net.kundzi.nbserver.message;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public interface MessageReader<T extends Message> {
  T read(ReadableByteChannel readableByteChannel) throws IOException;
}
