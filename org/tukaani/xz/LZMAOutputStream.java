package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToStream;

public class LZMAOutputStream extends FinishableOutputStream {
  private OutputStream out;
  
  private final ArrayCache arrayCache;
  
  private LZEncoder lz;
  
  private final RangeEncoderToStream rc;
  
  private LZMAEncoder lzma;
  
  private final int props;
  
  private final boolean useEndMarker;
  
  private final long expectedUncompressedSize;
  
  private long currentUncompressedSize = 0L;
  
  private boolean finished = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  private LZMAOutputStream(OutputStream paramOutputStream, LZMA2Options paramLZMA2Options, boolean paramBoolean1, boolean paramBoolean2, long paramLong, ArrayCache paramArrayCache) throws IOException {
    if (paramOutputStream == null)
      throw new NullPointerException(); 
    if (paramLong < -1L)
      throw new IllegalArgumentException("Invalid expected input size (less than -1)"); 
    this.useEndMarker = paramBoolean2;
    this.expectedUncompressedSize = paramLong;
    this.arrayCache = paramArrayCache;
    this.out = paramOutputStream;
    this.rc = new RangeEncoderToStream(paramOutputStream);
    int i = paramLZMA2Options.getDictSize();
    this.lzma = LZMAEncoder.getInstance((RangeEncoder)this.rc, paramLZMA2Options.getLc(), paramLZMA2Options.getLp(), paramLZMA2Options.getPb(), paramLZMA2Options.getMode(), i, 0, paramLZMA2Options.getNiceLen(), paramLZMA2Options.getMatchFinder(), paramLZMA2Options.getDepthLimit(), paramArrayCache);
    this.lz = this.lzma.getLZEncoder();
    byte[] arrayOfByte = paramLZMA2Options.getPresetDict();
    if (arrayOfByte != null && arrayOfByte.length > 0) {
      if (paramBoolean1)
        throw new UnsupportedOptionsException("Preset dictionary cannot be used in .lzma files (try a raw LZMA stream instead)"); 
      this.lz.setPresetDict(i, arrayOfByte);
    } 
    this.props = (paramLZMA2Options.getPb() * 5 + paramLZMA2Options.getLp()) * 9 + paramLZMA2Options.getLc();
    if (paramBoolean1) {
      paramOutputStream.write(this.props);
      byte b;
      for (b = 0; b < 4; b++) {
        paramOutputStream.write(i & 0xFF);
        i >>>= 8;
      } 
      for (b = 0; b < 8; b++)
        paramOutputStream.write((int)(paramLong >>> 8 * b) & 0xFF); 
    } 
  }
  
  public LZMAOutputStream(OutputStream paramOutputStream, LZMA2Options paramLZMA2Options, long paramLong) throws IOException {
    this(paramOutputStream, paramLZMA2Options, paramLong, ArrayCache.getDefaultCache());
  }
  
  public LZMAOutputStream(OutputStream paramOutputStream, LZMA2Options paramLZMA2Options, long paramLong, ArrayCache paramArrayCache) throws IOException {
    this(paramOutputStream, paramLZMA2Options, true, (paramLong == -1L), paramLong, paramArrayCache);
  }
  
  public LZMAOutputStream(OutputStream paramOutputStream, LZMA2Options paramLZMA2Options, boolean paramBoolean) throws IOException {
    this(paramOutputStream, paramLZMA2Options, paramBoolean, ArrayCache.getDefaultCache());
  }
  
  public LZMAOutputStream(OutputStream paramOutputStream, LZMA2Options paramLZMA2Options, boolean paramBoolean, ArrayCache paramArrayCache) throws IOException {
    this(paramOutputStream, paramLZMA2Options, false, paramBoolean, -1L, paramArrayCache);
  }
  
  public int getProps() {
    return this.props;
  }
  
  public long getUncompressedSize() {
    return this.currentUncompressedSize;
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
    if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize - this.currentUncompressedSize < paramInt2)
      throw new XZIOException("Expected uncompressed input size (" + this.expectedUncompressedSize + " bytes) was exceeded"); 
    this.currentUncompressedSize += paramInt2;
    try {
      while (paramInt2 > 0) {
        int i = this.lz.fillWindow(paramArrayOfbyte, paramInt1, paramInt2);
        paramInt1 += i;
        paramInt2 -= i;
        this.lzma.encodeForLZMA1();
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  public void flush() throws IOException {
    throw new XZIOException("LZMAOutputStream does not support flushing");
  }
  
  public void finish() throws IOException {
    if (!this.finished) {
      if (this.exception != null)
        throw this.exception; 
      try {
        if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize != this.currentUncompressedSize)
          throw new XZIOException("Expected uncompressed size (" + this.expectedUncompressedSize + ") doesn't equal the number of bytes written to the stream (" + this.currentUncompressedSize + ")"); 
        this.lz.setFinishing();
        this.lzma.encodeForLZMA1();
        if (this.useEndMarker)
          this.lzma.encodeLZMA1EndMarker(); 
        this.rc.finish();
      } catch (IOException iOException) {
        this.exception = iOException;
        throw iOException;
      } 
      this.finished = true;
      this.lzma.putArraysToCache(this.arrayCache);
      this.lzma = null;
      this.lz = null;
    } 
  }
  
  public void close() throws IOException {
    if (this.out != null) {
      try {
        finish();
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
