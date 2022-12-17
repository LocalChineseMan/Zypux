package org.tukaani.xz.rangecoder;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.CorruptedInputException;

public final class RangeDecoderFromBuffer extends RangeDecoder {
  private static final int INIT_SIZE = 5;
  
  private final byte[] buf;
  
  private int pos;
  
  public RangeDecoderFromBuffer(int paramInt, ArrayCache paramArrayCache) {
    this.buf = paramArrayCache.getByteArray(paramInt - 5, false);
    this.pos = this.buf.length;
  }
  
  public void putArraysToCache(ArrayCache paramArrayCache) {
    paramArrayCache.putArray(this.buf);
  }
  
  public void prepareInputBuffer(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    if (paramInt < 5)
      throw new CorruptedInputException(); 
    if (paramDataInputStream.readUnsignedByte() != 0)
      throw new CorruptedInputException(); 
    this.code = paramDataInputStream.readInt();
    this.range = -1;
    paramInt -= 5;
    this.pos = this.buf.length - paramInt;
    paramDataInputStream.readFully(this.buf, this.pos, paramInt);
  }
  
  public boolean isFinished() {
    return (this.pos == this.buf.length && this.code == 0);
  }
  
  public void normalize() throws IOException {
    if ((this.range & 0xFF000000) == 0)
      try {
        this.code = this.code << 8 | this.buf[this.pos++] & 0xFF;
        this.range <<= 8;
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new CorruptedInputException();
      }  
  }
}
