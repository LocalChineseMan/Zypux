package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.index.IndexHash;

public class SingleXZInputStream extends InputStream {
  private InputStream in;
  
  private final ArrayCache arrayCache;
  
  private final int memoryLimit;
  
  private final StreamFlags streamHeaderFlags;
  
  private final Check check;
  
  private final boolean verifyCheck;
  
  private BlockInputStream blockDecoder = null;
  
  private final IndexHash indexHash = new IndexHash();
  
  private boolean endReached = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  private static byte[] readStreamHeader(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[12];
    (new DataInputStream(paramInputStream)).readFully(arrayOfByte);
    return arrayOfByte;
  }
  
  public SingleXZInputStream(InputStream paramInputStream) throws IOException {
    this(paramInputStream, -1);
  }
  
  public SingleXZInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, -1, paramArrayCache);
  }
  
  public SingleXZInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    this(paramInputStream, paramInt, true);
  }
  
  public SingleXZInputStream(InputStream paramInputStream, int paramInt, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, paramInt, true, paramArrayCache);
  }
  
  public SingleXZInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean) throws IOException {
    this(paramInputStream, paramInt, paramBoolean, ArrayCache.getDefaultCache());
  }
  
  public SingleXZInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, paramInt, paramBoolean, readStreamHeader(paramInputStream), paramArrayCache);
  }
  
  SingleXZInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean, byte[] paramArrayOfbyte, ArrayCache paramArrayCache) throws IOException {
    this.arrayCache = paramArrayCache;
    this.in = paramInputStream;
    this.memoryLimit = paramInt;
    this.verifyCheck = paramBoolean;
    this.streamHeaderFlags = DecoderUtil.decodeStreamHeader(paramArrayOfbyte);
    this.check = Check.getInstance(this.streamHeaderFlags.checkType);
  }
  
  public int getCheckType() {
    return this.streamHeaderFlags.checkType;
  }
  
  public String getCheckName() {
    return this.check.getName();
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
    int i = 0;
    try {
      while (paramInt2 > 0) {
        if (this.blockDecoder == null)
          try {
            this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, -1L, -1L, this.arrayCache);
          } catch (IndexIndicatorException indexIndicatorException) {
            this.indexHash.validate(this.in);
            validateStreamFooter();
            this.endReached = true;
            return i ? i : -1;
          }  
        int j = this.blockDecoder.read(paramArrayOfbyte, paramInt1, paramInt2);
        if (j > 0) {
          i += j;
          paramInt1 += j;
          paramInt2 -= j;
          continue;
        } 
        if (j == -1) {
          this.indexHash.add(this.blockDecoder.getUnpaddedSize(), this.blockDecoder.getUncompressedSize());
          this.blockDecoder = null;
        } 
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      if (i == 0)
        throw iOException; 
    } 
    return i;
  }
  
  private void validateStreamFooter() throws IOException {
    byte[] arrayOfByte = new byte[12];
    (new DataInputStream(this.in)).readFully(arrayOfByte);
    StreamFlags streamFlags = DecoderUtil.decodeStreamFooter(arrayOfByte);
    if (!DecoderUtil.areStreamFlagsEqual(this.streamHeaderFlags, streamFlags) || this.indexHash.getIndexSize() != streamFlags.backwardSize)
      throw new CorruptedInputException("XZ Stream Footer does not match Stream Header"); 
  }
  
  public int available() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    return (this.blockDecoder == null) ? 0 : this.blockDecoder.available();
  }
  
  public void close() throws IOException {
    close(true);
  }
  
  public void close(boolean paramBoolean) throws IOException {
    if (this.in != null) {
      if (this.blockDecoder != null) {
        this.blockDecoder.close();
        this.blockDecoder = null;
      } 
      try {
        if (paramBoolean)
          this.in.close(); 
      } finally {
        this.in = null;
      } 
    } 
  }
}
