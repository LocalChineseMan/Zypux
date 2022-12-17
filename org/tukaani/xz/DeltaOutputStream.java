package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.delta.DeltaEncoder;

class DeltaOutputStream extends FinishableOutputStream {
  private static final int FILTER_BUF_SIZE = 4096;
  
  private FinishableOutputStream out;
  
  private final DeltaEncoder delta;
  
  private final byte[] filterBuf = new byte[4096];
  
  private boolean finished = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  static int getMemoryUsage() {
    return 5;
  }
  
  DeltaOutputStream(FinishableOutputStream paramFinishableOutputStream, DeltaOptions paramDeltaOptions) {
    this.out = paramFinishableOutputStream;
    this.delta = new DeltaEncoder(paramDeltaOptions.getDistance());
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
      throw new XZIOException("Stream finished"); 
    try {
      while (paramInt2 > 4096) {
        this.delta.encode(paramArrayOfbyte, paramInt1, 4096, this.filterBuf);
        this.out.write(this.filterBuf);
        paramInt1 += 4096;
        paramInt2 -= 4096;
      } 
      this.delta.encode(paramArrayOfbyte, paramInt1, paramInt2, this.filterBuf);
      this.out.write(this.filterBuf, 0, paramInt2);
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  public void flush() throws IOException {
    if (this.exception != null)
      throw this.exception; 
    if (this.finished)
      throw new XZIOException("Stream finished or closed"); 
    try {
      this.out.flush();
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  public void finish() throws IOException {
    if (!this.finished) {
      if (this.exception != null)
        throw this.exception; 
      try {
        this.out.finish();
      } catch (IOException iOException) {
        this.exception = iOException;
        throw iOException;
      } 
      this.finished = true;
    } 
  }
  
  public void close() throws IOException {
    if (this.out != null) {
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
