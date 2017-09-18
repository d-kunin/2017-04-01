package net.kundzi.nbserver.message.lvmessage;

import net.kundzi.nbserver.message.Message;

/**
 * Length Value Message
 * A message with length
 */
public interface LvMessage extends Message {
  int length();
}
