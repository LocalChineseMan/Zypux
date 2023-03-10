package io.netty.handler.codec.spdy;

public interface SpdyStreamFrame extends SpdyFrame {
  int streamId();
  
  SpdyStreamFrame setStreamId(int paramInt);
  
  boolean isLast();
  
  SpdyStreamFrame setLast(boolean paramBoolean);
}
