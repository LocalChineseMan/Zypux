package io.netty.channel;

public interface RecvByteBufAllocator {
  Handle newHandle();
  
  public static interface RecvByteBufAllocator {}
}
