package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.lz.LZDecoder;
import org.tukaani.xz.lzma.LZMADecoder;
import org.tukaani.xz.rangecoder.RangeDecoder;
import org.tukaani.xz.rangecoder.RangeDecoderFromStream;

public class LZMAInputStream extends InputStream {
  public static final int DICT_SIZE_MAX = 2147483632;
  
  private InputStream in;
  
  private ArrayCache arrayCache;
  
  private LZDecoder lz;
  
  private RangeDecoderFromStream rc;
  
  private LZMADecoder lzma;
  
  private boolean endReached = false;
  
  private boolean relaxedEndCondition = false;
  
  private final byte[] tempBuf = new byte[1];
  
  private long remainingSize;
  
  private IOException exception = null;
  
  public static int getMemoryUsage(int paramInt, byte paramByte) throws UnsupportedOptionsException, CorruptedInputException {
    if (paramInt < 0 || paramInt > 2147483632)
      throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation"); 
    int i = paramByte & 0xFF;
    if (i > 224)
      throw new CorruptedInputException("Invalid LZMA properties byte"); 
    i %= 45;
    int j = i / 9;
    int k = i - j * 9;
    return getMemoryUsage(paramInt, k, j);
  }
  
  public static int getMemoryUsage(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 < 0 || paramInt2 > 8 || paramInt3 < 0 || paramInt3 > 4)
      throw new IllegalArgumentException("Invalid lc or lp"); 
    return 10 + getDictSize(paramInt1) / 1024 + (1536 << paramInt2 + paramInt3) / 1024;
  }
  
  private static int getDictSize(int paramInt) {
    if (paramInt < 0 || paramInt > 2147483632)
      throw new IllegalArgumentException("LZMA dictionary is too big for this implementation"); 
    if (paramInt < 4096)
      paramInt = 4096; 
    return paramInt + 15 & 0xFFFFFFF0;
  }
  
  public LZMAInputStream(InputStream paramInputStream) throws IOException {
    this(paramInputStream, -1);
  }
  
  public LZMAInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, -1, paramArrayCache);
  }
  
  public LZMAInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    this(paramInputStream, paramInt, ArrayCache.getDefaultCache());
  }
  
  public LZMAInputStream(InputStream paramInputStream, int paramInt, ArrayCache paramArrayCache) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    byte b = dataInputStream.readByte();
    int i = 0;
    for (byte b1 = 0; b1 < 4; b1++)
      i |= dataInputStream.readUnsignedByte() << 8 * b1; 
    long l = 0L;
    int j;
    for (j = 0; j < 8; j++)
      l |= dataInputStream.readUnsignedByte() << 8 * j; 
    j = getMemoryUsage(i, b);
    if (paramInt != -1 && j > paramInt)
      throw new MemoryLimitException(j, paramInt); 
    initialize(paramInputStream, l, b, i, null, paramArrayCache);
  }
  
  public LZMAInputStream(InputStream paramInputStream, long paramLong, byte paramByte, int paramInt) throws IOException {
    initialize(paramInputStream, paramLong, paramByte, paramInt, null, ArrayCache.getDefaultCache());
  }
  
  public LZMAInputStream(InputStream paramInputStream, long paramLong, byte paramByte, int paramInt, byte[] paramArrayOfbyte) throws IOException {
    initialize(paramInputStream, paramLong, paramByte, paramInt, paramArrayOfbyte, ArrayCache.getDefaultCache());
  }
  
  public LZMAInputStream(InputStream paramInputStream, long paramLong, byte paramByte, int paramInt, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) throws IOException {
    initialize(paramInputStream, paramLong, paramByte, paramInt, paramArrayOfbyte, paramArrayCache);
  }
  
  public LZMAInputStream(InputStream paramInputStream, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfbyte) throws IOException {
    initialize(paramInputStream, paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfbyte, ArrayCache.getDefaultCache());
  }
  
  public LZMAInputStream(InputStream paramInputStream, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) throws IOException {
    initialize(paramInputStream, paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfbyte, paramArrayCache);
  }
  
  private void initialize(InputStream paramInputStream, long paramLong, byte paramByte, int paramInt, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) throws IOException {
    if (paramLong < -1L)
      throw new UnsupportedOptionsException("Uncompressed size is too big"); 
    int i = paramByte & 0xFF;
    if (i > 224)
      throw new CorruptedInputException("Invalid LZMA properties byte"); 
    int j = i / 45;
    i -= j * 9 * 5;
    int k = i / 9;
    int m = i - k * 9;
    if (paramInt < 0 || paramInt > 2147483632)
      throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation"); 
    initialize(paramInputStream, paramLong, m, k, j, paramInt, paramArrayOfbyte, paramArrayCache);
  }
  
  private void initialize(InputStream paramInputStream, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) throws IOException {
    if (paramLong < -1L || paramInt1 < 0 || paramInt1 > 8 || paramInt2 < 0 || paramInt2 > 4 || paramInt3 < 0 || paramInt3 > 4)
      throw new IllegalArgumentException(); 
    this.in = paramInputStream;
    this.arrayCache = paramArrayCache;
    paramInt4 = getDictSize(paramInt4);
    if (paramLong >= 0L && paramInt4 > paramLong)
      paramInt4 = getDictSize((int)paramLong); 
    this.lz = new LZDecoder(getDictSize(paramInt4), paramArrayOfbyte, paramArrayCache);
    this.rc = new RangeDecoderFromStream(paramInputStream);
    this.lzma = new LZMADecoder(this.lz, (RangeDecoder)this.rc, paramInt1, paramInt2, paramInt3);
    this.remainingSize = paramLong;
  }
  
  public void enableRelaxedEndCondition() {
    this.relaxedEndCondition = true;
  }
  
  public int read() throws IOException {
    return (read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    if (this.endReached)
      return -1; 
    try {
      int i = 0;
      while (paramInt2 > 0) {
        int j = paramInt2;
        if (this.remainingSize >= 0L && this.remainingSize < paramInt2)
          j = (int)this.remainingSize; 
        this.lz.setLimit(j);
        try {
          this.lzma.decode();
        } catch (CorruptedInputException corruptedInputException) {
          if (this.remainingSize != -1L || !this.lzma.endMarkerDetected())
            throw corruptedInputException; 
          this.endReached = true;
          this.rc.normalize();
        } 
        int k = this.lz.flush(paramArrayOfbyte, paramInt1);
        paramInt1 += k;
        paramInt2 -= k;
        i += k;
        if (this.remainingSize >= 0L) {
          this.remainingSize -= k;
          assert this.remainingSize >= 0L;
          if (this.remainingSize == 0L)
            this.endReached = true; 
        } 
        if (this.endReached) {
          if (this.lz.hasPending() || (!this.relaxedEndCondition && !this.rc.isFinished()))
            throw new CorruptedInputException(); 
          putArraysToCache();
          return (i == 0) ? -1 : i;
        } 
      } 
      return i;
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  private void putArraysToCache() {
    if (this.lz != null) {
      this.lz.putArraysToCache(this.arrayCache);
      this.lz = null;
    } 
  }
  
  public void close() throws IOException {
    if (this.in != null) {
      putArraysToCache();
      try {
        this.in.close();
      } finally {
        this.in = null;
      } 
    } 
  }
}
