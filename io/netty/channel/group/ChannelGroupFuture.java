package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Iterator;

public interface ChannelGroupFuture extends Future<Void>, Iterable<ChannelFuture> {
  ChannelGroup group();
  
  ChannelFuture find(Channel paramChannel);
  
  boolean isSuccess();
  
  ChannelGroupException cause();
  
  boolean isPartialSuccess();
  
  boolean isPartialFailure();
  
  ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> paramGenericFutureListener);
  
  ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... paramVarArgs);
  
  ChannelGroupFuture await() throws InterruptedException;
  
  ChannelGroupFuture awaitUninterruptibly();
  
  ChannelGroupFuture syncUninterruptibly();
  
  ChannelGroupFuture sync() throws InterruptedException;
  
  Iterator<ChannelFuture> iterator();
}
