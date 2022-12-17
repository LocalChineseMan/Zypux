package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleInputStream extends InputStream {
  private static final int FILTER_BUF_SIZE = 4096;
  
  private InputStream in;
  
  private final SimpleFilter simpleFilter;
  
  private final byte[] filterBuf = new byte[4096];
  
  private int pos = 0;
  
  private int filtered = 0;
  
  private int unfiltered = 0;
  
  private boolean endReached = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  static int getMemoryUsage() {
    return 5;
  }
  
  SimpleInputStream(InputStream paramInputStream, SimpleFilter paramSimpleFilter) {
    if (paramInputStream == null)
      throw new NullPointerException(); 
    assert paramSimpleFilter != null;
    this.in = paramInputStream;
    this.simpleFilter = paramSimpleFilter;
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
    try {
      int i = 0;
      while (true) {
        int j = Math.min(this.filtered, paramInt2);
        System.arraycopy(this.filterBuf, this.pos, paramArrayOfbyte, paramInt1, j);
        this.pos += j;
        this.filtered -= j;
        paramInt1 += j;
        paramInt2 -= j;
        i += j;
        if (this.pos + this.filtered + this.unfiltered == 4096) {
          System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.filtered + this.unfiltered);
          this.pos = 0;
        } 
        if (paramInt2 == 0 || this.endReached)
          return (i > 0) ? i : -1; 
        assert this.filtered == 0;
        int k = 4096 - this.pos + this.filtered + this.unfiltered;
        k = this.in.read(this.filterBuf, this.pos + this.filtered + this.unfiltered, k);
        if (k == -1) {
          this.endReached = true;
          this.filtered = this.unfiltered;
          this.unfiltered = 0;
          continue;
        } 
        this.unfiltered += k;
        this.filtered = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);
        assert this.filtered <= this.unfiltered;
        this.unfiltered -= this.filtered;
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  public int available() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    return this.filtered;
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
