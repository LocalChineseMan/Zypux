package org.tukaani.xz.rangecoder;

import java.io.IOException;

public abstract class RangeDecoder extends RangeCoder {
  int range = 0;
  
  int code = 0;
  
  public abstract void normalize() throws IOException;
  
  public int decodeBit(short[] paramArrayOfshort, int paramInt) throws IOException {
    boolean bool;
    normalize();
    short s = paramArrayOfshort[paramInt];
    int i = (this.range >>> 11) * s;
    if ((this.code ^ Integer.MIN_VALUE) < (i ^ Integer.MIN_VALUE)) {
      this.range = i;
      paramArrayOfshort[paramInt] = (short)(s + (2048 - s >>> 5));
      bool = false;
    } else {
      this.range -= i;
      this.code -= i;
      paramArrayOfshort[paramInt] = (short)(s - (s >>> 5));
      bool = true;
    } 
    return bool;
  }
  
  public int decodeBitTree(short[] paramArrayOfshort) throws IOException {
    int i = 1;
    while (true) {
      i = i << 1 | decodeBit(paramArrayOfshort, i);
      if (i >= paramArrayOfshort.length)
        return i - paramArrayOfshort.length; 
    } 
  }
  
  public int decodeReverseBitTree(short[] paramArrayOfshort) throws IOException {
    int i = 1;
    byte b = 0;
    int j = 0;
    while (true) {
      int k = decodeBit(paramArrayOfshort, i);
      i = i << 1 | k;
      j |= k << b++;
      if (i >= paramArrayOfshort.length)
        return j; 
    } 
  }
  
  public int decodeDirectBits(int paramInt) throws IOException {
    int i = 0;
    while (true) {
      normalize();
      this.range >>>= 1;
      int j = this.code - this.range >>> 31;
      this.code -= this.range & j - 1;
      i = i << 1 | 1 - j;
      if (--paramInt == 0)
        return i; 
    } 
  }
}
