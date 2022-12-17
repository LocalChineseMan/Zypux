package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XZInputStream extends InputStream {
  private final ArrayCache arrayCache;
  
  private final int memoryLimit;
  
  private InputStream in;
  
  private SingleXZInputStream xzIn;
  
  private final boolean verifyCheck;
  
  private boolean endReached = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  public XZInputStream(InputStream paramInputStream) throws IOException {
    this(paramInputStream, -1);
  }
  
  public XZInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, -1, paramArrayCache);
  }
  
  public XZInputStream(InputStream paramInputStream, int paramInt) throws IOException {
    this(paramInputStream, paramInt, true);
  }
  
  public XZInputStream(InputStream paramInputStream, int paramInt, ArrayCache paramArrayCache) throws IOException {
    this(paramInputStream, paramInt, true, paramArrayCache);
  }
  
  public XZInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean) throws IOException {
    this(paramInputStream, paramInt, paramBoolean, ArrayCache.getDefaultCache());
  }
  
  public XZInputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean, ArrayCache paramArrayCache) throws IOException {
    this.arrayCache = paramArrayCache;
    this.in = paramInputStream;
    this.memoryLimit = paramInt;
    this.verifyCheck = paramBoolean;
    this.xzIn = new SingleXZInputStream(paramInputStream, paramInt, paramBoolean, paramArrayCache);
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
        if (this.xzIn == null) {
          prepareNextStream();
          if (this.endReached)
            return !i ? -1 : i; 
        } 
        int j = this.xzIn.read(paramArrayOfbyte, paramInt1, paramInt2);
        if (j > 0) {
          i += j;
          paramInt1 += j;
          paramInt2 -= j;
          continue;
        } 
        if (j == -1)
          this.xzIn = null; 
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      if (i == 0)
        throw iOException; 
    } 
    return i;
  }
  
  private void prepareNextStream() throws IOException {
    DataInputStream dataInputStream = new DataInputStream(this.in);
    byte[] arrayOfByte = new byte[12];
    do {
      int i = dataInputStream.read(arrayOfByte, 0, 1);
      if (i == -1) {
        this.endReached = true;
        return;
      } 
      dataInputStream.readFully(arrayOfByte, 1, 3);
    } while (arrayOfByte[0] == 0 && arrayOfByte[1] == 0 && arrayOfByte[2] == 0 && arrayOfByte[3] == 0);
    dataInputStream.readFully(arrayOfByte, 4, 8);
    try {
      this.xzIn = new SingleXZInputStream(this.in, this.memoryLimit, this.verifyCheck, arrayOfByte, this.arrayCache);
    } catch (XZFormatException xZFormatException) {
      throw new CorruptedInputException("Garbage after a valid XZ Stream");
    } 
  }
  
  public int available() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    return (this.xzIn == null) ? 0 : this.xzIn.available();
  }
  
  public void close() throws IOException {
    close(true);
  }
  
  public void close(boolean paramBoolean) throws IOException {
    if (this.in != null) {
      if (this.xzIn != null) {
        this.xzIn.close(false);
        this.xzIn = null;
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
