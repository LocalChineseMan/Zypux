package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.delta.DeltaDecoder;

public class DeltaInputStream extends InputStream {
  public static final int DISTANCE_MIN = 1;
  
  public static final int DISTANCE_MAX = 256;
  
  private InputStream in;
  
  private final DeltaDecoder delta;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  public DeltaInputStream(InputStream paramInputStream, int paramInt) {
    if (paramInputStream == null)
      throw new NullPointerException(); 
    this.in = paramInputStream;
    this.delta = new DeltaDecoder(paramInt);
  }
  
  public int read() throws IOException {
    return (read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i;
    if (paramInt2 == 0)
      return 0; 
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    try {
      i = this.in.read(paramArrayOfbyte, paramInt1, paramInt2);
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
    if (i == -1)
      return -1; 
    this.delta.decode(paramArrayOfbyte, paramInt1, i);
    return i;
  }
  
  public int available() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    return this.in.available();
  }
  
  public void close() throws IOException {
    if (this.in != null)
      try {
        this.in.close();
      } finally {
        this.in = null;
      }  
  }
}
