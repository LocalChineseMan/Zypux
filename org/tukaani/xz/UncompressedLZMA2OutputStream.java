package org.tukaani.xz;

import java.io.DataOutputStream;
import java.io.IOException;

class UncompressedLZMA2OutputStream extends FinishableOutputStream {
  private final ArrayCache arrayCache;
  
  private FinishableOutputStream out;
  
  private final DataOutputStream outData;
  
  private final byte[] uncompBuf;
  
  private int uncompPos = 0;
  
  private boolean dictResetNeeded = true;
  
  private boolean finished = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  static int getMemoryUsage() {
    return 70;
  }
  
  UncompressedLZMA2OutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    if (paramFinishableOutputStream == null)
      throw new NullPointerException(); 
    this.out = paramFinishableOutputStream;
    this.outData = new DataOutputStream(paramFinishableOutputStream);
    this.arrayCache = paramArrayCache;
    this.uncompBuf = paramArrayCache.getByteArray(65536, false);
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
    try {
      while (paramInt2 > 0) {
        int i = Math.min(65536 - this.uncompPos, paramInt2);
        System.arraycopy(paramArrayOfbyte, paramInt1, this.uncompBuf, this.uncompPos, i);
        paramInt2 -= i;
        this.uncompPos += i;
        if (this.uncompPos == 65536)
          writeChunk(); 
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  private void writeChunk() throws IOException {
    this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
    this.outData.writeShort(this.uncompPos - 1);
    this.outData.write(this.uncompBuf, 0, this.uncompPos);
    this.uncompPos = 0;
    this.dictResetNeeded = false;
  }
  
  private void writeEndMarker() throws IOException {
    if (this.exception != null)
      throw this.exception; 
    if (this.finished)
      throw new XZIOException("Stream finished or closed"); 
    try {
      if (this.uncompPos > 0)
        writeChunk(); 
      this.out.write(0);
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
    this.finished = true;
    this.arrayCache.putArray(this.uncompBuf);
  }
  
  public void flush() throws IOException {
    if (this.exception != null)
      throw this.exception; 
    if (this.finished)
      throw new XZIOException("Stream finished or closed"); 
    try {
      if (this.uncompPos > 0)
        writeChunk(); 
      this.out.flush();
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  public void finish() throws IOException {
    if (!this.finished) {
      writeEndMarker();
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
          writeEndMarker();
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
