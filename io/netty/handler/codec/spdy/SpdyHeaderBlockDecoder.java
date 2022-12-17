package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;

abstract class SpdyHeaderBlockDecoder {
  static SpdyHeaderBlockDecoder newInstance(SpdyVersion spdyVersion, int maxHeaderSize) {
    return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
  }
  
  abstract void decode(ByteBuf paramByteBuf, SpdyHeadersFrame paramSpdyHeadersFrame) throws Exception;
  
  abstract void endHeaderBlock(SpdyHeadersFrame paramSpdyHeadersFrame) throws Exception;
  
  abstract void end();
}
