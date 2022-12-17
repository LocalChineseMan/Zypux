package org.tukaani.xz.lz;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.CorruptedInputException;

public final class LZDecoder {
  private final byte[] buf;
  
  private final int bufSize;
  
  private int start = 0;
  
  private int pos = 0;
  
  private int full = 0;
  
  private int limit = 0;
  
  private int pendingLen = 0;
  
  private int pendingDist = 0;
  
  public LZDecoder(int paramInt, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) {
    this.bufSize = paramInt;
    this.buf = paramArrayCache.getByteArray(this.bufSize, false);
    if (paramArrayOfbyte != null) {
      this.pos = Math.min(paramArrayOfbyte.length, paramInt);
      this.full = this.pos;
      this.start = this.pos;
      System.arraycopy(paramArrayOfbyte, paramArrayOfbyte.length - this.pos, this.buf, 0, this.pos);
    } 
  }
  
  public void putArraysToCache(ArrayCache paramArrayCache) {
    paramArrayCache.putArray(this.buf);
  }
  
  public void reset() {
    this.start = 0;
    this.pos = 0;
    this.full = 0;
    this.limit = 0;
    this.buf[this.bufSize - 1] = 0;
  }
  
  public void setLimit(int paramInt) {
    if (this.bufSize - this.pos <= paramInt) {
      this.limit = this.bufSize;
    } else {
      this.limit = this.pos + paramInt;
    } 
  }
  
  public boolean hasSpace() {
    return (this.pos < this.limit);
  }
  
  public boolean hasPending() {
    return (this.pendingLen > 0);
  }
  
  public int getPos() {
    return this.pos;
  }
  
  public int getByte(int paramInt) {
    int i = this.pos - paramInt - 1;
    if (paramInt >= this.pos)
      i += this.bufSize; 
    return this.buf[i] & 0xFF;
  }
  
  public void putByte(byte paramByte) {
    this.buf[this.pos++] = paramByte;
    if (this.full < this.pos)
      this.full = this.pos; 
  }
  
  public void repeat(int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt1 >= this.full)
      throw new CorruptedInputException(); 
    int i = Math.min(this.limit - this.pos, paramInt2);
    this.pendingLen = paramInt2 - i;
    this.pendingDist = paramInt1;
    int j = this.pos - paramInt1 - 1;
    if (j < 0) {
      assert this.full == this.bufSize;
      j += this.bufSize;
      int k = Math.min(this.bufSize - j, i);
      assert k <= paramInt1 + 1;
      System.arraycopy(this.buf, j, this.buf, this.pos, k);
      this.pos += k;
      j = 0;
      i -= k;
      if (i == 0)
        return; 
    } 
    assert j < this.pos;
    assert i > 0;
    while (true) {
      int k = Math.min(i, this.pos - j);
      System.arraycopy(this.buf, j, this.buf, this.pos, k);
      this.pos += k;
      i -= k;
      if (i <= 0) {
        if (this.full < this.pos)
          this.full = this.pos; 
        return;
      } 
    } 
  }
  
  public void repeatPending() throws IOException {
    if (this.pendingLen > 0)
      repeat(this.pendingDist, this.pendingLen); 
  }
  
  public void copyUncompressed(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    int i = Math.min(this.bufSize - this.pos, paramInt);
    paramDataInputStream.readFully(this.buf, this.pos, i);
    this.pos += i;
    if (this.full < this.pos)
      this.full = this.pos; 
  }
  
  public int flush(byte[] paramArrayOfbyte, int paramInt) {
    int i = this.pos - this.start;
    if (this.pos == this.bufSize)
      this.pos = 0; 
    System.arraycopy(this.buf, this.start, paramArrayOfbyte, paramInt, i);
    this.start = this.pos;
    return i;
  }
}
