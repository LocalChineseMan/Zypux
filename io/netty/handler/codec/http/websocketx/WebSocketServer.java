package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;

class null implements ChannelFutureListener {
  public void operationComplete(ChannelFuture future) throws Exception {
    if (future.isSuccess()) {
      ChannelPipeline p = future.channel().pipeline();
      p.remove(encoderName);
      promise.setSuccess();
    } else {
      promise.setFailure(future.cause());
    } 
  }
}
