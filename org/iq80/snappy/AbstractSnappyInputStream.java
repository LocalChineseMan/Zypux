package org.iq80.snappy;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

abstract class AbstractSnappyInputStream extends InputStream {
  private final InputStream in;
  
  private final byte[] frameHeader;
  
  private final boolean verifyChecksums;
  
  private final BufferRecycler recycler;
  
  private byte[] input;
  
  private byte[] uncompressed;
  
  private boolean closed;
  
  private boolean eof;
  
  private int valid;
  
  private int position;
  
  private byte[] buffer;
  
  public AbstractSnappyInputStream(InputStream in, int maxBlockSize, int frameHeaderSize, boolean verifyChecksums, byte[] expectedHeader) throws IOException {
    this.in = in;
    this.verifyChecksums = verifyChecksums;
    this.recycler = BufferRecycler.instance();
    allocateBuffersBasedOnSize(maxBlockSize + 5);
    this.frameHeader = new byte[frameHeaderSize];
    byte[] actualHeader = new byte[expectedHeader.length];
    int read = SnappyInternalUtils.readBytes(in, actualHeader, 0, actualHeader.length);
    if (read < expectedHeader.length)
      throw new EOFException("encountered EOF while reading stream header"); 
    if (!Arrays.equals(expectedHeader, actualHeader))
      throw new IOException("invalid stream header"); 
  }
  
  private void allocateBuffersBasedOnSize(int size) {
    this.input = this.recycler.allocInputBuffer(size);
    this.uncompressed = this.recycler.allocDecodeBuffer(size);
  }
  
  public int read() throws IOException {
    if (this.closed)
      return -1; 
    if (!ensureBuffer())
      return -1; 
    return this.buffer[this.position++] & 0xFF;
  }
  
  public int read(byte[] output, int offset, int length) throws IOException {
    SnappyInternalUtils.checkNotNull(output, "output is null", new Object[0]);
    SnappyInternalUtils.checkPositionIndexes(offset, offset + length, output.length);
    if (this.closed)
      throw new IOException("Stream is closed"); 
    if (length == 0)
      return 0; 
    if (!ensureBuffer())
      return -1; 
    int size = Math.min(length, available());
    System.arraycopy(this.buffer, this.position, output, offset, size);
    this.position += size;
    return size;
  }
  
  public int available() throws IOException {
    if (this.closed)
      return 0; 
    return this.valid - this.position;
  }
  
  public void close() throws IOException {
    try {
      this.in.close();
    } finally {
      if (!this.closed) {
        this.closed = true;
        this.recycler.releaseInputBuffer(this.input);
        this.recycler.releaseDecodeBuffer(this.uncompressed);
      } 
    } 
  }
  
  enum FrameAction {
    RAW, SKIP, UNCOMPRESS;
  }
  
  public static final class FrameMetaData {
    final int length;
    
    final AbstractSnappyInputStream.FrameAction frameAction;
    
    public FrameMetaData(AbstractSnappyInputStream.FrameAction frameAction, int length) {
      this.frameAction = frameAction;
      this.length = length;
    }
  }
  
  public static final class FrameData {
    final int checkSum;
    
    final int offset;
    
    public FrameData(int checkSum, int offset) {
      this.checkSum = checkSum;
      this.offset = offset;
    }
  }
  
  private boolean ensureBuffer() throws IOException {
    if (available() > 0)
      return true; 
    if (this.eof)
      return false; 
    if (!readBlockHeader()) {
      this.eof = true;
      return false;
    } 
    FrameMetaData frameMetaData = getFrameMetaData(this.frameHeader);
    if (FrameAction.SKIP == frameMetaData.frameAction) {
      SnappyInternalUtils.skip(this.in, frameMetaData.length);
      return ensureBuffer();
    } 
    if (frameMetaData.length > this.input.length)
      allocateBuffersBasedOnSize(frameMetaData.length); 
    int actualRead = SnappyInternalUtils.readBytes(this.in, this.input, 0, frameMetaData.length);
    if (actualRead != frameMetaData.length)
      throw new EOFException("unexpectd EOF when reading frame"); 
    FrameData frameData = getFrameData(this.frameHeader, this.input, actualRead);
    if (FrameAction.UNCOMPRESS == frameMetaData.frameAction) {
      int uncompressedLength = Snappy.getUncompressedLength(this.input, frameData.offset);
      if (uncompressedLength > this.uncompressed.length)
        this.uncompressed = this.recycler.allocDecodeBuffer(uncompressedLength); 
      this.valid = Snappy.uncompress(this.input, frameData.offset, actualRead - frameData.offset, this.uncompressed, 0);
      this.buffer = this.uncompressed;
      this.position = 0;
    } else {
      this.position = frameData.offset;
      this.buffer = this.input;
      this.valid = actualRead;
    } 
    if (this.verifyChecksums) {
      int actualCrc32c = Crc32C.maskedCrc32c(this.buffer, this.position, this.valid - this.position);
      if (frameData.checkSum != actualCrc32c)
        throw new IOException("Corrupt input: invalid checksum"); 
    } 
    return true;
  }
  
  protected abstract FrameMetaData getFrameMetaData(byte[] paramArrayOfbyte) throws IOException;
  
  protected abstract FrameData getFrameData(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt);
  
  private boolean readBlockHeader() throws IOException {
    int read = SnappyInternalUtils.readBytes(this.in, this.frameHeader, 0, this.frameHeader.length);
    if (read == -1)
      return false; 
    if (read < this.frameHeader.length)
      throw new EOFException("encountered EOF while reading block header"); 
    return true;
  }
}
