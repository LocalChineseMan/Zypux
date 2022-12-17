package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.SctpServerChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;

final class OioSctpServerChannelConfig extends DefaultSctpServerChannelConfig {
  private OioSctpServerChannelConfig(OioSctpServerChannel channel, SctpServerChannel javaChannel) {
    super(channel, javaChannel);
  }
  
  protected void autoReadCleared() {
    OioSctpServerChannel.access$100(OioSctpServerChannel.this, false);
  }
}
