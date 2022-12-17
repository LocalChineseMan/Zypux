package io.netty.channel;

public interface MessageSizeEstimator {
  Handle newHandle();
  
  public static interface MessageSizeEstimator {}
}
