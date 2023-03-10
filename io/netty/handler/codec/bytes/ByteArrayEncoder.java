package io.netty.handler.codec.bytes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder<byte[]> {
  protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
    out.add(Unpooled.wrappedBuffer(msg));
  }
}
