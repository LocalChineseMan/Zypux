package org.tukaani.xz.index;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.SeekableInputStream;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;

public class IndexDecoder extends IndexBase {
  private final StreamFlags streamFlags;
  
  private final long streamPadding;
  
  private final int memoryUsage;
  
  private final long[] unpadded;
  
  private final long[] uncompressed;
  
  private long largestBlockSize = 0L;
  
  private int recordOffset = 0;
  
  private long compressedOffset = 0L;
  
  private long uncompressedOffset = 0L;
  
  public IndexDecoder(SeekableInputStream paramSeekableInputStream, StreamFlags paramStreamFlags, long paramLong, int paramInt) throws IOException {
    super((XZIOException)new CorruptedInputException("XZ Index is corrupt"));
    this.streamFlags = paramStreamFlags;
    this.streamPadding = paramLong;
    long l1 = paramSeekableInputStream.position() + paramStreamFlags.backwardSize - 4L;
    CRC32 cRC32 = new CRC32();
    CheckedInputStream checkedInputStream = new CheckedInputStream((InputStream)paramSeekableInputStream, cRC32);
    if (checkedInputStream.read() != 0)
      throw new CorruptedInputException("XZ Index is corrupt"); 
    try {
      long l = DecoderUtil.decodeVLI(checkedInputStream);
      if (l >= paramStreamFlags.backwardSize / 2L)
        throw new CorruptedInputException("XZ Index is corrupt"); 
      if (l > 2147483647L)
        throw new UnsupportedOptionsException("XZ Index has over 2147483647 Records"); 
      this.memoryUsage = 1 + (int)((16L * l + 1023L) / 1024L);
      if (paramInt >= 0 && this.memoryUsage > paramInt)
        throw new MemoryLimitException(this.memoryUsage, paramInt); 
      this.unpadded = new long[(int)l];
      this.uncompressed = new long[(int)l];
      byte b1 = 0;
      for (int j = (int)l; j > 0; j--) {
        long l3 = DecoderUtil.decodeVLI(checkedInputStream);
        long l4 = DecoderUtil.decodeVLI(checkedInputStream);
        if (paramSeekableInputStream.position() > l1)
          throw new CorruptedInputException("XZ Index is corrupt"); 
        this.unpadded[b1] = this.blocksSum + l3;
        this.uncompressed[b1] = this.uncompressedSum + l4;
        b1++;
        add(l3, l4);
        assert b1 == this.recordCount;
        if (this.largestBlockSize < l4)
          this.largestBlockSize = l4; 
      } 
    } catch (EOFException eOFException) {
      throw new CorruptedInputException("XZ Index is corrupt");
    } 
    int i = getIndexPaddingSize();
    if (paramSeekableInputStream.position() + i != l1)
      throw new CorruptedInputException("XZ Index is corrupt"); 
    while (i-- > 0) {
      if (checkedInputStream.read() != 0)
        throw new CorruptedInputException("XZ Index is corrupt"); 
    } 
    long l2 = cRC32.getValue();
    for (byte b = 0; b < 4; b++) {
      if ((l2 >>> b * 8 & 0xFFL) != paramSeekableInputStream.read())
        throw new CorruptedInputException("XZ Index is corrupt"); 
    } 
  }
  
  public void setOffsets(IndexDecoder paramIndexDecoder) {
    paramIndexDecoder.recordOffset += (int)paramIndexDecoder.recordCount;
    this.compressedOffset = paramIndexDecoder.compressedOffset + paramIndexDecoder.getStreamSize() + paramIndexDecoder.streamPadding;
    assert (this.compressedOffset & 0x3L) == 0L;
    paramIndexDecoder.uncompressedOffset += paramIndexDecoder.uncompressedSum;
  }
  
  public int getMemoryUsage() {
    return this.memoryUsage;
  }
  
  public StreamFlags getStreamFlags() {
    return this.streamFlags;
  }
  
  public int getRecordCount() {
    return (int)this.recordCount;
  }
  
  public long getUncompressedSize() {
    return this.uncompressedSum;
  }
  
  public long getLargestBlockSize() {
    return this.largestBlockSize;
  }
  
  public boolean hasUncompressedOffset(long paramLong) {
    return (paramLong >= this.uncompressedOffset && paramLong < this.uncompressedOffset + this.uncompressedSum);
  }
  
  public boolean hasRecord(int paramInt) {
    return (paramInt >= this.recordOffset && paramInt < this.recordOffset + this.recordCount);
  }
  
  public void locateBlock(BlockInfo paramBlockInfo, long paramLong) {
    assert paramLong >= this.uncompressedOffset;
    paramLong -= this.uncompressedOffset;
    assert paramLong < this.uncompressedSum;
    int i = 0;
    int j;
    for (j = this.unpadded.length - 1; i < j; j = k) {
      int k = i + (j - i) / 2;
      if (this.uncompressed[k] <= paramLong) {
        i = k + 1;
        continue;
      } 
    } 
    setBlockInfo(paramBlockInfo, this.recordOffset + i);
  }
  
  public void setBlockInfo(BlockInfo paramBlockInfo, int paramInt) {
    assert paramInt >= this.recordOffset;
    assert (paramInt - this.recordOffset) < this.recordCount;
    paramBlockInfo.index = this;
    paramBlockInfo.blockNumber = paramInt;
    int i = paramInt - this.recordOffset;
    if (i == 0) {
      paramBlockInfo.compressedOffset = 0L;
      paramBlockInfo.uncompressedOffset = 0L;
    } else {
      paramBlockInfo.compressedOffset = this.unpadded[i - 1] + 3L & 0xFFFFFFFFFFFFFFFCL;
      paramBlockInfo.uncompressedOffset = this.uncompressed[i - 1];
    } 
    paramBlockInfo.unpaddedSize = this.unpadded[i] - paramBlockInfo.compressedOffset;
    paramBlockInfo.uncompressedSize = this.uncompressed[i] - paramBlockInfo.uncompressedOffset;
    paramBlockInfo.compressedOffset += this.compressedOffset + 12L;
    paramBlockInfo.uncompressedOffset += this.uncompressedOffset;
  }
}
