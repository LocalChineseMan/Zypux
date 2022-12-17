package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.index.BlockInfo;
import org.tukaani.xz.index.IndexDecoder;

public class SeekableXZInputStream extends SeekableInputStream {
  private final ArrayCache arrayCache;
  
  private SeekableInputStream in;
  
  private final int memoryLimit;
  
  private int indexMemoryUsage = 0;
  
  private final ArrayList<IndexDecoder> streams = new ArrayList<>();
  
  private int checkTypes = 0;
  
  private long uncompressedSize = 0L;
  
  private long largestBlockSize = 0L;
  
  private int blockCount = 0;
  
  private final BlockInfo curBlockInfo;
  
  private final BlockInfo queriedBlockInfo;
  
  private Check check;
  
  private final boolean verifyCheck;
  
  private BlockInputStream blockDecoder = null;
  
  private long curPos = 0L;
  
  private long seekPos;
  
  private boolean seekNeeded = false;
  
  private boolean endReached = false;
  
  private IOException exception = null;
  
  private final byte[] tempBuf = new byte[1];
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream) throws IOException {
    this(paramSeekableInputStream, -1);
  }
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream, ArrayCache paramArrayCache) throws IOException {
    this(paramSeekableInputStream, -1, paramArrayCache);
  }
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream, int paramInt) throws IOException {
    this(paramSeekableInputStream, paramInt, true);
  }
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream, int paramInt, ArrayCache paramArrayCache) throws IOException {
    this(paramSeekableInputStream, paramInt, true, paramArrayCache);
  }
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream, int paramInt, boolean paramBoolean) throws IOException {
    this(paramSeekableInputStream, paramInt, paramBoolean, ArrayCache.getDefaultCache());
  }
  
  public SeekableXZInputStream(SeekableInputStream paramSeekableInputStream, int paramInt, boolean paramBoolean, ArrayCache paramArrayCache) throws IOException {
    this.arrayCache = paramArrayCache;
    this.verifyCheck = paramBoolean;
    this.in = paramSeekableInputStream;
    DataInputStream dataInputStream = new DataInputStream(paramSeekableInputStream);
    paramSeekableInputStream.seek(0L);
    byte[] arrayOfByte1 = new byte[XZ.HEADER_MAGIC.length];
    dataInputStream.readFully(arrayOfByte1);
    if (!Arrays.equals(arrayOfByte1, XZ.HEADER_MAGIC))
      throw new XZFormatException(); 
    long l1 = paramSeekableInputStream.length();
    if ((l1 & 0x3L) != 0L)
      throw new CorruptedInputException("XZ file size is not a multiple of 4 bytes"); 
    byte[] arrayOfByte2 = new byte[12];
    long l2;
    for (l2 = 0L; l1 > 0L; l2 = 0L) {
      IndexDecoder indexDecoder;
      if (l1 < 12L)
        throw new CorruptedInputException(); 
      paramSeekableInputStream.seek(l1 - 12L);
      dataInputStream.readFully(arrayOfByte2);
      if (arrayOfByte2[8] == 0 && arrayOfByte2[9] == 0 && arrayOfByte2[10] == 0 && arrayOfByte2[11] == 0) {
        l2 += 4L;
        l1 -= 4L;
        continue;
      } 
      l1 -= 12L;
      StreamFlags streamFlags1 = DecoderUtil.decodeStreamFooter(arrayOfByte2);
      if (streamFlags1.backwardSize >= l1)
        throw new CorruptedInputException("Backward Size in XZ Stream Footer is too big"); 
      this.check = Check.getInstance(streamFlags1.checkType);
      this.checkTypes |= 1 << streamFlags1.checkType;
      paramSeekableInputStream.seek(l1 - streamFlags1.backwardSize);
      try {
        indexDecoder = new IndexDecoder(paramSeekableInputStream, streamFlags1, l2, paramInt);
      } catch (MemoryLimitException memoryLimitException) {
        assert paramInt >= 0;
        throw new MemoryLimitException(memoryLimitException.getMemoryNeeded() + this.indexMemoryUsage, paramInt + this.indexMemoryUsage);
      } 
      this.indexMemoryUsage += indexDecoder.getMemoryUsage();
      if (paramInt >= 0) {
        paramInt -= indexDecoder.getMemoryUsage();
        assert paramInt >= 0;
      } 
      if (this.largestBlockSize < indexDecoder.getLargestBlockSize())
        this.largestBlockSize = indexDecoder.getLargestBlockSize(); 
      long l = indexDecoder.getStreamSize() - 12L;
      if (l1 < l)
        throw new CorruptedInputException("XZ Index indicates too big compressed size for the XZ Stream"); 
      l1 -= l;
      paramSeekableInputStream.seek(l1);
      dataInputStream.readFully(arrayOfByte2);
      StreamFlags streamFlags2 = DecoderUtil.decodeStreamHeader(arrayOfByte2);
      if (!DecoderUtil.areStreamFlagsEqual(streamFlags2, streamFlags1))
        throw new CorruptedInputException("XZ Stream Footer does not match Stream Header"); 
      this.uncompressedSize += indexDecoder.getUncompressedSize();
      if (this.uncompressedSize < 0L)
        throw new UnsupportedOptionsException("XZ file is too big"); 
      this.blockCount += indexDecoder.getRecordCount();
      if (this.blockCount < 0)
        throw new UnsupportedOptionsException("XZ file has over 2147483647 Blocks"); 
      this.streams.add(indexDecoder);
    } 
    assert l1 == 0L;
    this.memoryLimit = paramInt;
    IndexDecoder indexDecoder1 = this.streams.get(this.streams.size() - 1);
    for (int i = this.streams.size() - 2; i >= 0; i--) {
      IndexDecoder indexDecoder = this.streams.get(i);
      indexDecoder.setOffsets(indexDecoder1);
      indexDecoder1 = indexDecoder;
    } 
    IndexDecoder indexDecoder2 = this.streams.get(this.streams.size() - 1);
    this.curBlockInfo = new BlockInfo(indexDecoder2);
    this.queriedBlockInfo = new BlockInfo(indexDecoder2);
  }
  
  public int getCheckTypes() {
    return this.checkTypes;
  }
  
  public int getIndexMemoryUsage() {
    return this.indexMemoryUsage;
  }
  
  public long getLargestBlockSize() {
    return this.largestBlockSize;
  }
  
  public int getStreamCount() {
    return this.streams.size();
  }
  
  public int getBlockCount() {
    return this.blockCount;
  }
  
  public long getBlockPos(int paramInt) {
    locateBlockByNumber(this.queriedBlockInfo, paramInt);
    return this.queriedBlockInfo.uncompressedOffset;
  }
  
  public long getBlockSize(int paramInt) {
    locateBlockByNumber(this.queriedBlockInfo, paramInt);
    return this.queriedBlockInfo.uncompressedSize;
  }
  
  public long getBlockCompPos(int paramInt) {
    locateBlockByNumber(this.queriedBlockInfo, paramInt);
    return this.queriedBlockInfo.compressedOffset;
  }
  
  public long getBlockCompSize(int paramInt) {
    locateBlockByNumber(this.queriedBlockInfo, paramInt);
    return this.queriedBlockInfo.unpaddedSize + 3L & 0xFFFFFFFFFFFFFFFCL;
  }
  
  public int getBlockCheckType(int paramInt) {
    locateBlockByNumber(this.queriedBlockInfo, paramInt);
    return this.queriedBlockInfo.getCheckType();
  }
  
  public int getBlockNumber(long paramLong) {
    locateBlockByPos(this.queriedBlockInfo, paramLong);
    return this.queriedBlockInfo.blockNumber;
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
    int i = 0;
    try {
      if (this.seekNeeded)
        seek(); 
      if (this.endReached)
        return -1; 
      while (paramInt2 > 0) {
        if (this.blockDecoder == null) {
          seek();
          if (this.endReached)
            break; 
        } 
        int j = this.blockDecoder.read(paramArrayOfbyte, paramInt1, paramInt2);
        if (j > 0) {
          this.curPos += j;
          i += j;
          paramInt1 += j;
          paramInt2 -= j;
          continue;
        } 
        if (j == -1)
          this.blockDecoder = null; 
      } 
    } catch (IOException iOException) {
      if (iOException instanceof java.io.EOFException)
        iOException = new CorruptedInputException(); 
      this.exception = iOException;
      if (i == 0)
        throw iOException; 
    } 
    return i;
  }
  
  public int available() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (this.exception != null)
      throw this.exception; 
    return (this.endReached || this.seekNeeded || this.blockDecoder == null) ? 0 : this.blockDecoder.available();
  }
  
  public void close() throws IOException {
    close(true);
  }
  
  public void close(boolean paramBoolean) throws IOException {
    if (this.in != null) {
      if (this.blockDecoder != null) {
        this.blockDecoder.close();
        this.blockDecoder = null;
      } 
      try {
        if (paramBoolean)
          this.in.close(); 
      } finally {
        this.in = null;
      } 
    } 
  }
  
  public long length() {
    return this.uncompressedSize;
  }
  
  public long position() throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    return this.seekNeeded ? this.seekPos : this.curPos;
  }
  
  public void seek(long paramLong) throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (paramLong < 0L)
      throw new XZIOException("Negative seek position: " + paramLong); 
    this.seekPos = paramLong;
    this.seekNeeded = true;
  }
  
  public void seekToBlock(int paramInt) throws IOException {
    if (this.in == null)
      throw new XZIOException("Stream closed"); 
    if (paramInt < 0 || paramInt >= this.blockCount)
      throw new XZIOException("Invalid XZ Block number: " + paramInt); 
    this.seekPos = getBlockPos(paramInt);
    this.seekNeeded = true;
  }
  
  private void seek() throws IOException {
    if (!this.seekNeeded) {
      if (this.curBlockInfo.hasNext()) {
        this.curBlockInfo.setNext();
        initBlockDecoder();
        return;
      } 
      this.seekPos = this.curPos;
    } 
    this.seekNeeded = false;
    if (this.seekPos >= this.uncompressedSize) {
      this.curPos = this.seekPos;
      if (this.blockDecoder != null) {
        this.blockDecoder.close();
        this.blockDecoder = null;
      } 
      this.endReached = true;
      return;
    } 
    this.endReached = false;
    locateBlockByPos(this.curBlockInfo, this.seekPos);
    if (this.curPos <= this.curBlockInfo.uncompressedOffset || this.curPos > this.seekPos) {
      this.in.seek(this.curBlockInfo.compressedOffset);
      this.check = Check.getInstance(this.curBlockInfo.getCheckType());
      initBlockDecoder();
      this.curPos = this.curBlockInfo.uncompressedOffset;
    } 
    if (this.seekPos > this.curPos) {
      long l = this.seekPos - this.curPos;
      if (this.blockDecoder.skip(l) != l)
        throw new CorruptedInputException(); 
      this.curPos = this.seekPos;
    } 
  }
  
  private void locateBlockByPos(BlockInfo paramBlockInfo, long paramLong) {
    if (paramLong < 0L || paramLong >= this.uncompressedSize)
      throw new IndexOutOfBoundsException("Invalid uncompressed position: " + paramLong); 
    for (byte b = 0;; b++) {
      IndexDecoder indexDecoder = this.streams.get(b);
      if (indexDecoder.hasUncompressedOffset(paramLong)) {
        indexDecoder.locateBlock(paramBlockInfo, paramLong);
        assert (paramBlockInfo.compressedOffset & 0x3L) == 0L;
        assert paramBlockInfo.uncompressedSize > 0L;
        assert paramLong >= paramBlockInfo.uncompressedOffset;
        assert paramLong < paramBlockInfo.uncompressedOffset + paramBlockInfo.uncompressedSize;
        return;
      } 
    } 
  }
  
  private void locateBlockByNumber(BlockInfo paramBlockInfo, int paramInt) {
    if (paramInt < 0 || paramInt >= this.blockCount)
      throw new IndexOutOfBoundsException("Invalid XZ Block number: " + paramInt); 
    if (paramBlockInfo.blockNumber == paramInt)
      return; 
    for (byte b = 0;; b++) {
      IndexDecoder indexDecoder = this.streams.get(b);
      if (indexDecoder.hasRecord(paramInt)) {
        indexDecoder.setBlockInfo(paramBlockInfo, paramInt);
        return;
      } 
    } 
  }
  
  private void initBlockDecoder() throws IOException {
    try {
      if (this.blockDecoder != null) {
        this.blockDecoder.close();
        this.blockDecoder = null;
      } 
      this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, this.curBlockInfo.unpaddedSize, this.curBlockInfo.uncompressedSize, this.arrayCache);
    } catch (MemoryLimitException memoryLimitException) {
      assert this.memoryLimit >= 0;
      throw new MemoryLimitException(memoryLimitException.getMemoryNeeded() + this.indexMemoryUsage, this.memoryLimit + this.indexMemoryUsage);
    } catch (IndexIndicatorException indexIndicatorException) {
      throw new CorruptedInputException();
    } 
  }
}
