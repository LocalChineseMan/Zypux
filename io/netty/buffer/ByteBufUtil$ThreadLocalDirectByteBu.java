package io.netty.buffer;

import io.netty.util.Recycler;

final class null extends Recycler<ByteBufUtil.ThreadLocalDirectByteBuf> {
  protected ByteBufUtil.ThreadLocalDirectByteBuf newObject(Recycler.Handle handle) {
    return new ByteBufUtil.ThreadLocalDirectByteBuf(handle, null);
  }
}
