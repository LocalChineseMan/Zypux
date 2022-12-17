package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface Handle {
  ByteBuf allocate(ByteBufAllocator paramByteBufAllocator);
  
  int guess();
  
  void record(int paramInt);
}
