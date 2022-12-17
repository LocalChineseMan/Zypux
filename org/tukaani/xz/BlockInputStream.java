package org.tukaani.xz;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;

class BlockInputStream extends InputStream {
  private final DataInputStream inData;
  
  private final CountingInputStream inCounted;
  
  private InputStream filterChain;
  
  private final Check check;
  
  private final boolean verifyCheck;
  
  private long uncompressedSizeInHeader = -1L;
  
  private long compressedSizeInHeader = -1L;
  
  private long compressedSizeLimit;
  
  private final int headerSize;
  
  private long uncompressedSize = 0L;
  
  private boolean endReached = false;
  
  private final byte[] tempBuf = new byte[1];
  
  public BlockInputStream(InputStream paramInputStream, Check paramCheck, boolean paramBoolean, int paramInt, long paramLong1, long paramLong2, ArrayCache paramArrayCache) throws IOException, IndexIndicatorException {
    this.check = paramCheck;
    this.verifyCheck = paramBoolean;
    this.inData = new DataInputStream(paramInputStream);
    int i = this.inData.readUnsignedByte();
    if (i == 0)
      throw new IndexIndicatorException(); 
    this.headerSize = 4 * (i + 1);
    byte[] arrayOfByte = new byte[this.headerSize];
    arrayOfByte[0] = (byte)i;
    this.inData.readFully(arrayOfByte, 1, this.headerSize - 1);
    if (!DecoderUtil.isCRC32Valid(arrayOfByte, 0, this.headerSize - 4, this.headerSize - 4))
      throw new CorruptedInputException("XZ Block Header is corrupt"); 
    if ((arrayOfByte[1] & 0x3C) != 0)
      throw new UnsupportedOptionsException("Unsupported options in XZ Block Header"); 
    int j = (arrayOfByte[1] & 0x3) + 1;
    long[] arrayOfLong = new long[j];
    byte[][] arrayOfByte1 = new byte[j][];
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte, 2, this.headerSize - 6);
    try {
      this.compressedSizeLimit = 9223372036854775804L - this.headerSize - paramCheck.getSize();
      if ((arrayOfByte[1] & 0x40) != 0) {
        this.compressedSizeInHeader = DecoderUtil.decodeVLI(byteArrayInputStream);
        if (this.compressedSizeInHeader == 0L || this.compressedSizeInHeader > this.compressedSizeLimit)
          throw new CorruptedInputException(); 
        this.compressedSizeLimit = this.compressedSizeInHeader;
      } 
      if ((arrayOfByte[1] & 0x80) != 0)
        this.uncompressedSizeInHeader = DecoderUtil.decodeVLI(byteArrayInputStream); 
      for (byte b = 0; b < j; b++) {
        arrayOfLong[b] = DecoderUtil.decodeVLI(byteArrayInputStream);
        long l = DecoderUtil.decodeVLI(byteArrayInputStream);
        if (l > byteArrayInputStream.available())
          throw new CorruptedInputException(); 
        arrayOfByte1[b] = new byte[(int)l];
        byteArrayInputStream.read(arrayOfByte1[b]);
      } 
    } catch (IOException iOException) {
      throw new CorruptedInputException("XZ Block Header is corrupt");
    } 
    int k;
    for (k = byteArrayInputStream.available(); k > 0; k--) {
      if (byteArrayInputStream.read() != 0)
        throw new UnsupportedOptionsException("Unsupported options in XZ Block Header"); 
    } 
    if (paramLong1 != -1L) {
      k = this.headerSize + paramCheck.getSize();
      if (k >= paramLong1)
        throw new CorruptedInputException("XZ Index does not match a Block Header"); 
      long l = paramLong1 - k;
      if (l > this.compressedSizeLimit || (this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != l))
        throw new CorruptedInputException("XZ Index does not match a Block Header"); 
      if (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != paramLong2)
        throw new CorruptedInputException("XZ Index does not match a Block Header"); 
      this.compressedSizeLimit = l;
      this.compressedSizeInHeader = l;
      this.uncompressedSizeInHeader = paramLong2;
    } 
    FilterDecoder[] arrayOfFilterDecoder = new FilterDecoder[arrayOfLong.length];
    int m;
    for (m = 0; m < arrayOfFilterDecoder.length; m++) {
      if (arrayOfLong[m] == 33L) {
        arrayOfFilterDecoder[m] = new LZMA2Decoder(arrayOfByte1[m]);
      } else if (arrayOfLong[m] == 3L) {
        arrayOfFilterDecoder[m] = new DeltaDecoder(arrayOfByte1[m]);
      } else if (BCJDecoder.isBCJFilterID(arrayOfLong[m])) {
        arrayOfFilterDecoder[m] = new BCJDecoder(arrayOfLong[m], arrayOfByte1[m]);
      } else {
        throw new UnsupportedOptionsException("Unknown Filter ID " + arrayOfLong[m]);
      } 
    } 
    RawCoder.validate((FilterCoder[])arrayOfFilterDecoder);
    if (paramInt >= 0) {
      m = 0;
      for (byte b = 0; b < arrayOfFilterDecoder.length; b++)
        m += arrayOfFilterDecoder[b].getMemoryUsage(); 
      if (m > paramInt)
        throw new MemoryLimitException(m, paramInt); 
    } 
    this.inCounted = new CountingInputStream(paramInputStream);
    this.filterChain = this.inCounted;
    for (m = arrayOfFilterDecoder.length - 1; m >= 0; m--)
      this.filterChain = arrayOfFilterDecoder[m].getInputStream(this.filterChain, paramArrayCache); 
  }
  
  public int read() throws IOException {
    return (read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.endReached)
      return -1; 
    int i = this.filterChain.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (i > 0) {
      if (this.verifyCheck)
        this.check.update(paramArrayOfbyte, paramInt1, i); 
      this.uncompressedSize += i;
      long l = this.inCounted.getSize();
      if (l < 0L || l > this.compressedSizeLimit || this.uncompressedSize < 0L || (this.uncompressedSizeInHeader != -1L && this.uncompressedSize > this.uncompressedSizeInHeader))
        throw new CorruptedInputException(); 
      if (i < paramInt2 || this.uncompressedSize == this.uncompressedSizeInHeader) {
        if (this.filterChain.read() != -1)
          throw new CorruptedInputException(); 
        validate();
        this.endReached = true;
      } 
    } else if (i == -1) {
      validate();
      this.endReached = true;
    } 
    return i;
  }
  
  private void validate() throws IOException {
    long l = this.inCounted.getSize();
    if ((this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != l) || (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != this.uncompressedSize))
      throw new CorruptedInputException(); 
    while ((l++ & 0x3L) != 0L) {
      if (this.inData.readUnsignedByte() != 0)
        throw new CorruptedInputException(); 
    } 
    byte[] arrayOfByte = new byte[this.check.getSize()];
    this.inData.readFully(arrayOfByte);
    if (this.verifyCheck && !Arrays.equals(this.check.finish(), arrayOfByte))
      throw new CorruptedInputException("Integrity check (" + this.check.getName() + ") does not match"); 
  }
  
  public int available() throws IOException {
    return this.filterChain.available();
  }
  
  public void close() {
    try {
      this.filterChain.close();
    } catch (IOException iOException) {
      assert false;
    } 
    this.filterChain = null;
  }
  
  public long getUnpaddedSize() {
    return this.headerSize + this.inCounted.getSize() + this.check.getSize();
  }
  
  public long getUncompressedSize() {
    return this.uncompressedSize;
  }
}
