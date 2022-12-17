package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.ArrayCache;

public final class RangeEncoderToBuffer extends RangeEncoder {
  private final byte[] buf;
  
  private int bufPos;
  
  public RangeEncoderToBuffer(int paramInt, ArrayCache paramArrayCache) {
    this.buf = paramArrayCache.getByteArray(paramInt, false);
    reset();
  }
  
  public void putArraysToCache(ArrayCache paramArrayCache) {
    paramArrayCache.putArray(this.buf);
  }
  
  public void reset() {
    super.reset();
    this.bufPos = 0;
  }
  
  public int getPendingSize() {
    return this.bufPos + (int)this.cacheSize + 5 - 1;
  }
  
  public int finish() {
    try {
      super.finish();
    } catch (IOException iOException) {
      throw new Error();
    } 
    return this.bufPos;
  }
  
  public void write(OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(this.buf, 0, this.bufPos);
  }
  
  void writeByte(int paramInt) {
    this.buf[this.bufPos++] = (byte)paramInt;
  }
}
