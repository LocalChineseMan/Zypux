package org.iq80.snappy;

import java.io.IOException;
import java.io.OutputStream;

abstract class AbstractSnappyOutputStream extends OutputStream {
  private final BufferRecycler recycler;
  
  private final int blockSize;
  
  private final byte[] buffer;
  
  private final byte[] outputBuffer;
  
  private final double minCompressionRatio;
  
  private final OutputStream out;
  
  private int position;
  
  private boolean closed;
  
  public AbstractSnappyOutputStream(OutputStream out, int blockSize, double minCompressionRatio) throws IOException {
    this.out = SnappyInternalUtils.<OutputStream>checkNotNull(out, "out is null", new Object[0]);
    SnappyInternalUtils.checkArgument((minCompressionRatio > 0.0D && minCompressionRatio <= 1.0D), "minCompressionRatio %1s must be between (0,1.0].", new Object[] { Double.valueOf(minCompressionRatio) });
    this.minCompressionRatio = minCompressionRatio;
    this.recycler = BufferRecycler.instance();
    this.blockSize = blockSize;
    this.buffer = this.recycler.allocOutputBuffer(blockSize);
    this.outputBuffer = this.recycler.allocEncodingBuffer(Snappy.maxCompressedLength(blockSize));
    writeHeader(out);
  }
  
  protected abstract void writeHeader(OutputStream paramOutputStream) throws IOException;
  
  public void write(int b) throws IOException {
    if (this.closed)
      throw new IOException("Stream is closed"); 
    if (this.position >= this.blockSize)
      flushBuffer(); 
    this.buffer[this.position++] = (byte)b;
  }
  
  public void write(byte[] input, int offset, int length) throws IOException {
    SnappyInternalUtils.checkNotNull(input, "input is null", new Object[0]);
    SnappyInternalUtils.checkPositionIndexes(offset, offset + length, input.length);
    if (this.closed)
      throw new IOException("Stream is closed"); 
    int free = this.blockSize - this.position;
    if (free >= length) {
      copyToBuffer(input, offset, length);
      return;
    } 
    if (this.position > 0) {
      copyToBuffer(input, offset, free);
      flushBuffer();
      offset += free;
      length -= free;
    } 
    while (length >= this.blockSize) {
      writeCompressed(input, offset, this.blockSize);
      offset += this.blockSize;
      length -= this.blockSize;
    } 
    copyToBuffer(input, offset, length);
  }
  
  public final void flush() throws IOException {
    if (this.closed)
      throw new IOException("Stream is closed"); 
    flushBuffer();
    this.out.flush();
  }
  
  public final void close() throws IOException {
    if (this.closed)
      return; 
    try {
      flush();
      this.out.close();
    } finally {
      this.closed = true;
      this.recycler.releaseOutputBuffer(this.outputBuffer);
      this.recycler.releaseEncodeBuffer(this.buffer);
    } 
  }
  
  private void copyToBuffer(byte[] input, int offset, int length) {
    System.arraycopy(input, offset, this.buffer, this.position, length);
    this.position += length;
  }
  
  private void flushBuffer() throws IOException {
    if (this.position > 0) {
      writeCompressed(this.buffer, 0, this.position);
      this.position = 0;
    } 
  }
  
  private void writeCompressed(byte[] input, int offset, int length) throws IOException {
    int crc32c = calculateCRC32C(input, offset, length);
    int compressed = Snappy.compress(input, offset, length, this.outputBuffer, 0);
    if (compressed / length <= this.minCompressionRatio) {
      writeBlock(this.out, this.outputBuffer, 0, compressed, true, crc32c);
    } else {
      writeBlock(this.out, input, offset, length, false, crc32c);
    } 
  }
  
  protected int calculateCRC32C(byte[] data, int offset, int length) {
    return Crc32C.maskedCrc32c(data, offset, length);
  }
  
  protected abstract void writeBlock(OutputStream paramOutputStream, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3) throws IOException;
}
