package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import java.net.SocketAddress;

public abstract class AbstractOioChannel extends AbstractChannel {
  protected static final int SO_TIMEOUT = 1000;
  
  private volatile boolean readPending;
  
  private final Runnable readTask = (Runnable)new Object(this);
  
  protected AbstractOioChannel(Channel parent) {
    super(parent);
  }
  
  protected AbstractChannel.AbstractUnsafe newUnsafe() {
    return (AbstractChannel.AbstractUnsafe)new DefaultOioUnsafe(this, null);
  }
  
  protected boolean isCompatible(EventLoop loop) {
    return loop instanceof io.netty.channel.ThreadPerChannelEventLoop;
  }
  
  protected abstract void doConnect(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2) throws Exception;
  
  protected void doBeginRead() throws Exception {
    if (isReadPending())
      return; 
    setReadPending(true);
    eventLoop().execute(this.readTask);
  }
  
  protected abstract void doRead();
  
  protected boolean isReadPending() {
    return this.readPending;
  }
  
  protected void setReadPending(boolean readPending) {
    this.readPending = readPending;
  }
  
  private final class AbstractOioChannel {}
}
