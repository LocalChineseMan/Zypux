package org.tukaani.xz.rangecoder;

import java.io.IOException;

public abstract class RangeEncoder extends RangeCoder {
  private static final int MOVE_REDUCING_BITS = 4;
  
  private static final int BIT_PRICE_SHIFT_BITS = 4;
  
  private static final int[] prices = new int[128];
  
  private long low;
  
  private int range;
  
  long cacheSize;
  
  private byte cache;
  
  public void reset() {
    this.low = 0L;
    this.range = -1;
    this.cache = 0;
    this.cacheSize = 1L;
  }
  
  public int getPendingSize() {
    throw new Error();
  }
  
  public int finish() throws IOException {
    for (byte b = 0; b < 5; b++)
      shiftLow(); 
    return -1;
  }
  
  abstract void writeByte(int paramInt) throws IOException;
  
  private void shiftLow() throws IOException {
    int i = (int)(this.low >>> 32L);
    if (i != 0 || this.low < 4278190080L) {
      char c;
      byte b = this.cache;
      do {
        writeByte(b + i);
        c = 'ÿ';
      } while (--this.cacheSize != 0L);
      this.cache = (byte)(int)(this.low >>> 24L);
    } 
    this.cacheSize++;
    this.low = (this.low & 0xFFFFFFL) << 8L;
  }
  
  public void encodeBit(short[] paramArrayOfshort, int paramInt1, int paramInt2) throws IOException {
    short s = paramArrayOfshort[paramInt1];
    int i = (this.range >>> 11) * s;
    if (paramInt2 == 0) {
      this.range = i;
      paramArrayOfshort[paramInt1] = (short)(s + (2048 - s >>> 5));
    } else {
      this.low += i & 0xFFFFFFFFL;
      this.range -= i;
      paramArrayOfshort[paramInt1] = (short)(s - (s >>> 5));
    } 
    if ((this.range & 0xFF000000) == 0) {
      this.range <<= 8;
      shiftLow();
    } 
  }
  
  public static int getBitPrice(int paramInt1, int paramInt2) {
    assert paramInt2 == 0 || paramInt2 == 1;
    return prices[(paramInt1 ^ -paramInt2 & 0x7FF) >>> 4];
  }
  
  public void encodeBitTree(short[] paramArrayOfshort, int paramInt) throws IOException {
    int i = 1;
    int j = paramArrayOfshort.length;
    do {
      j >>>= 1;
      int k = paramInt & j;
      encodeBit(paramArrayOfshort, i, k);
      i <<= 1;
      if (k == 0)
        continue; 
      i |= 0x1;
    } while (j != 1);
  }
  
  public static int getBitTreePrice(short[] paramArrayOfshort, int paramInt) {
    int i = 0;
    paramInt |= paramArrayOfshort.length;
    while (true) {
      int j = paramInt & 0x1;
      paramInt >>>= 1;
      i += getBitPrice(paramArrayOfshort[paramInt], j);
      if (paramInt == 1)
        return i; 
    } 
  }
  
  public void encodeReverseBitTree(short[] paramArrayOfshort, int paramInt) throws IOException {
    int i = 1;
    paramInt |= paramArrayOfshort.length;
    do {
      int j = paramInt & 0x1;
      paramInt >>>= 1;
      encodeBit(paramArrayOfshort, i, j);
      i = i << 1 | j;
    } while (paramInt != 1);
  }
  
  public static int getReverseBitTreePrice(short[] paramArrayOfshort, int paramInt) {
    int i = 0;
    int j = 1;
    paramInt |= paramArrayOfshort.length;
    while (true) {
      int k = paramInt & 0x1;
      paramInt >>>= 1;
      i += getBitPrice(paramArrayOfshort[j], k);
      j = j << 1 | k;
      if (paramInt == 1)
        return i; 
    } 
  }
  
  public void encodeDirectBits(int paramInt1, int paramInt2) throws IOException {
    do {
      this.range >>>= 1;
      this.low += (this.range & 0 - (paramInt1 >>> --paramInt2 & 0x1));
      if ((this.range & 0xFF000000) != 0)
        continue; 
      this.range <<= 8;
      shiftLow();
    } while (paramInt2 != 0);
  }
  
  public static int getDirectBitsPrice(int paramInt) {
    return paramInt << 4;
  }
  
  static {
    for (byte b = 8; b < 'ࠀ'; b += 16) {
      int i = b;
      int j = 0;
      for (byte b1 = 0; b1 < 4; b1++) {
        i *= i;
        for (j <<= 1; (i & 0xFFFF0000) != 0; j++)
          i >>>= 1; 
      } 
      prices[b >> 4] = 161 - j;
    } 
  }
}
