package io.netty.channel.nio;

import io.netty.channel.Channel;
import java.nio.channels.SelectableChannel;

public interface NioUnsafe extends Channel.Unsafe {
  SelectableChannel ch();
  
  void finishConnect();
  
  void read();
  
  void forceFlush();
  
  public static interface AbstractNioChannel {}
}
