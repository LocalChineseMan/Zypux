package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder<WebSocketFrame> {
  protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
    if (frame instanceof PingWebSocketFrame) {
      frame.content().retain();
      ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
      return;
    } 
    if (frame instanceof PongWebSocketFrame)
      return; 
    out.add(frame.retain());
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
}
