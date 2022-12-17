package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

final class HandleImpl implements RecvByteBufAllocator.Handle {
  private final int minIndex;
  
  private final int maxIndex;
  
  private int index;
  
  private int nextReceiveBufferSize;
  
  private boolean decreaseNow;
  
  HandleImpl(int minIndex, int maxIndex, int initial) {
    this.minIndex = minIndex;
    this.maxIndex = maxIndex;
    this.index = AdaptiveRecvByteBufAllocator.access$000(initial);
    this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.access$100()[this.index];
  }
  
  public ByteBuf allocate(ByteBufAllocator alloc) {
    return alloc.ioBuffer(this.nextReceiveBufferSize);
  }
  
  public int guess() {
    return this.nextReceiveBufferSize;
  }
  
  public void record(int actualReadBytes) {
    if (actualReadBytes <= AdaptiveRecvByteBufAllocator.access$100()[Math.max(0, this.index - 1 - 1)]) {
      if (this.decreaseNow) {
        this.index = Math.max(this.index - 1, this.minIndex);
        this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.access$100()[this.index];
        this.decreaseNow = false;
      } else {
        this.decreaseNow = true;
      } 
    } else if (actualReadBytes >= this.nextReceiveBufferSize) {
      this.index = Math.min(this.index + 4, this.maxIndex);
      this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.access$100()[this.index];
      this.decreaseNow = false;
    } 
  }
}
