package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleOutputStream extends FinishableOutputStream {
  private static final int FILTER_BUF_SIZE = 4096;
  
  private FinishableOutputStream out;
  
  private final SimpleFilter simpleFilter;
  
  private final byte[] filterBuf = new byte[4096];
  
  private int pos = 0;
  
  private int unfiltered = 0;
  
  private IOException exception = null;
  
  private boolean finished = false;
  
  private final byte[] tempBuf = new byte[1];
  
  static int getMemoryUsage() {
    return 5;
  }
  
  SimpleOutputStream(FinishableOutputStream paramFinishableOutputStream, SimpleFilter paramSimpleFilter) {
    if (paramFinishableOutputStream == null)
      throw new NullPointerException(); 
    this.out = paramFinishableOutputStream;
    this.simpleFilter = paramSimpleFilter;
  }
  
  public void write(int paramInt) throws IOException {
    this.tempBuf[0] = (byte)paramInt;
    write(this.tempBuf, 0, 1);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new IndexOutOfBoundsException(); 
    if (this.exception != null)
      throw this.exception; 
    if (this.finished)
      throw new XZIOException("Stream finished or closed"); 
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, 4096 - this.pos + this.unfiltered);
      System.arraycopy(paramArrayOfbyte, paramInt1, this.filterBuf, this.pos + this.unfiltered, i);
      paramInt1 += i;
      paramInt2 -= i;
      this.unfiltered += i;
      int j = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);
      assert j <= this.unfiltered;
      this.unfiltered -= j;
      try {
        this.out.write(this.filterBuf, this.pos, j);
      } catch (IOException iOException) {
        this.exception = iOException;
        throw iOException;
      } 
      this.pos += j;
      if (this.pos + this.unfiltered == 4096) {
        System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.unfiltered);
        this.pos = 0;
      } 
    } 
  }
  
  private void writePending() throws IOException {
    assert !this.finished;
    if (this.exception != null)
      throw this.exception; 
    try {
      this.out.write(this.filterBuf, this.pos, this.unfiltered);
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
    this.finished = true;
  }
  
  public void flush() throws IOException {
    throw new UnsupportedOptionsException("Flushing is not supported");
  }
  
  public void finish() throws IOException {
    if (!this.finished) {
      writePending();
      try {
        this.out.finish();
      } catch (IOException iOException) {
        this.exception = iOException;
        throw iOException;
      } 
    } 
  }
  
  public void close() throws IOException {
    if (this.out != null) {
      if (!this.finished)
        try {
          writePending();
        } catch (IOException iOException) {} 
      try {
        this.out.close();
      } catch (IOException iOException) {
        if (this.exception == null)
          this.exception = iOException; 
      } 
      this.out = null;
    } 
    if (this.exception != null)
      throw this.exception; 
  }
}
