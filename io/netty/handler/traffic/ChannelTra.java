package io.netty.handler.traffic;

import io.netty.channel.ChannelPromise;

final class ToSend {
  final long date;
  
  final Object toSend;
  
  final ChannelPromise promise;
  
  private ToSend(long delay, Object toSend, ChannelPromise promise) {
    this.date = System.currentTimeMillis() + delay;
    this.toSend = toSend;
    this.promise = promise;
  }
}
