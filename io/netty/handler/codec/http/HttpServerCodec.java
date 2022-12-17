package io.netty.handler.codec.http;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.CombinedChannelDuplexHandler;

public final class HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> {
  public HttpServerCodec() {
    this(4096, 8192, 8192);
  }
  
  public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
    super((ChannelInboundHandler)new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize), (ChannelOutboundHandler)new HttpResponseEncoder());
  }
  
  public HttpServerCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
    super((ChannelInboundHandler)new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), (ChannelOutboundHandler)new HttpResponseEncoder());
  }
}
