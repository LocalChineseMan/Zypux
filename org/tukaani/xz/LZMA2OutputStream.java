package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToBuffer;

class LZMA2OutputStream extends FinishableOutputStream {
  static final int COMPRESSED_SIZE_MAX = 65536;
  
  private final ArrayCache arrayCache;
  
  private FinishableOutputStream out;
  
  private LZEncoder lz;
  
  private RangeEncoderToBuffer rc;
  
  private LZMAEncoder lzma;
  
  private final int props;
  
  private boolean dictResetNeeded = true;
  
  private boolean stateResetNeeded = true;
  
  private boolean propsNeeded = true;
  
  private int pendingSize = 0;
  
  private boolean finished = false;
  
  private IOException exception = null;
  
  private final byte[] chunkHeader = new byte[6];
  
  private final byte[] tempBuf = new byte[1];
  
  private static int getExtraSizeBefore(int paramInt) {
    return (65536 > paramInt) ? (65536 - paramInt) : 0;
  }
  
  static int getMemoryUsage(LZMA2Options paramLZMA2Options) {
    int i = paramLZMA2Options.getDictSize();
    int j = getExtraSizeBefore(i);
    return 70 + LZMAEncoder.getMemoryUsage(paramLZMA2Options.getMode(), i, j, paramLZMA2Options.getMatchFinder());
  }
  
  LZMA2OutputStream(FinishableOutputStream paramFinishableOutputStream, LZMA2Options paramLZMA2Options, ArrayCache paramArrayCache) {
    if (paramFinishableOutputStream == null)
      throw new NullPointerException(); 
    this.arrayCache = paramArrayCache;
    this.out = paramFinishableOutputStream;
    this.rc = new RangeEncoderToBuffer(65536, paramArrayCache);
    int i = paramLZMA2Options.getDictSize();
    int j = getExtraSizeBefore(i);
    this.lzma = LZMAEncoder.getInstance((RangeEncoder)this.rc, paramLZMA2Options.getLc(), paramLZMA2Options.getLp(), paramLZMA2Options.getPb(), paramLZMA2Options.getMode(), i, j, paramLZMA2Options.getNiceLen(), paramLZMA2Options.getMatchFinder(), paramLZMA2Options.getDepthLimit(), this.arrayCache);
    this.lz = this.lzma.getLZEncoder();
    byte[] arrayOfByte = paramLZMA2Options.getPresetDict();
    if (arrayOfByte != null && arrayOfByte.length > 0) {
      this.lz.setPresetDict(i, arrayOfByte);
      this.dictResetNeeded = false;
    } 
    this.props = (paramLZMA2Options.getPb() * 5 + paramLZMA2Options.getLp()) * 9 + paramLZMA2Options.getLc();
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
        int i = this.lz.fillWindow(paramArrayOfbyte, paramInt1, paramInt2);
        paramInt1 += i;
        paramInt2 -= i;
        this.pendingSize += i;
        if (this.lzma.encodeForLZMA2())
          writeChunk(); 
      } 
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
  }
  
  private void writeChunk() throws IOException {
    int i = this.rc.finish();
    int j = this.lzma.getUncompressedSize();
    assert i > 0 : i;
    assert j > 0 : j;
    if (i + 2 < j) {
      writeLZMA(j, i);
    } else {
      this.lzma.reset();
      j = this.lzma.getUncompressedSize();
      assert j > 0 : j;
      writeUncompressed(j);
    } 
    this.pendingSize -= j;
    this.lzma.resetUncompressedSize();
    this.rc.reset();
  }
  
  private void writeLZMA(int paramInt1, int paramInt2) throws IOException {
    int i;
    if (this.propsNeeded) {
      if (this.dictResetNeeded) {
        i = 224;
      } else {
        i = 192;
      } 
    } else if (this.stateResetNeeded) {
      i = 160;
    } else {
      i = 128;
    } 
    i |= paramInt1 - 1 >>> 16;
    this.chunkHeader[0] = (byte)i;
    this.chunkHeader[1] = (byte)(paramInt1 - 1 >>> 8);
    this.chunkHeader[2] = (byte)(paramInt1 - 1);
    this.chunkHeader[3] = (byte)(paramInt2 - 1 >>> 8);
    this.chunkHeader[4] = (byte)(paramInt2 - 1);
    if (this.propsNeeded) {
      this.chunkHeader[5] = (byte)this.props;
      this.out.write(this.chunkHeader, 0, 6);
    } else {
      this.out.write(this.chunkHeader, 0, 5);
    } 
    this.rc.write(this.out);
    this.propsNeeded = false;
    this.stateResetNeeded = false;
    this.dictResetNeeded = false;
  }
  
  private void writeUncompressed(int paramInt) throws IOException {
    while (paramInt > 0) {
      int i = Math.min(paramInt, 65536);
      this.chunkHeader[0] = (byte)(this.dictResetNeeded ? 1 : 2);
      this.chunkHeader[1] = (byte)(i - 1 >>> 8);
      this.chunkHeader[2] = (byte)(i - 1);
      this.out.write(this.chunkHeader, 0, 3);
      this.lz.copyUncompressed(this.out, paramInt, i);
      paramInt -= i;
      this.dictResetNeeded = false;
    } 
    this.stateResetNeeded = true;
  }
  
  private void writeEndMarker() throws IOException {
    assert !this.finished;
    if (this.exception != null)
      throw this.exception; 
    this.lz.setFinishing();
    try {
      while (this.pendingSize > 0) {
        this.lzma.encodeForLZMA2();
        writeChunk();
      } 
      this.out.write(0);
    } catch (IOException iOException) {
      this.exception = iOException;
      throw iOException;
    } 
    this.finished = true;
    this.lzma.putArraysToCache(this.arrayCache);
    this.lzma = null;
    this.lz = null;
    this.rc.putArraysToCache(this.arrayCache);
    this.rc = null;
  }
  
  public void flush() throws IOException {
    if (this.exception != null)
      throw this.exception; 
    if (this.finished)
      throw new XZIOException("Stream finished or closed"); 
    try {
      this.lz.setFlushing();
      while (this.pendingSize > 0) {
        this.lzma.encodeForLZMA2();
        writeChunk();
      } 
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
