package org.iq80.snappy;

import java.io.IOException;
import java.io.OutputStream;

public final class SnappyFramedOutputStream extends AbstractSnappyOutputStream {
  public static final int MAX_BLOCK_SIZE = 65536;
  
  public static final int DEFAULT_BLOCK_SIZE = 65536;
  
  public static final double DEFAULT_MIN_COMPRESSION_RATIO = 0.85D;
  
  public SnappyFramedOutputStream(OutputStream out) throws IOException {
    this(out, 65536, 0.85D);
  }
  
  public SnappyFramedOutputStream(OutputStream out, int blockSize, double minCompressionRatio) throws IOException {
    super(out, blockSize, minCompressionRatio);
    SnappyInternalUtils.checkArgument((blockSize > 0 && blockSize <= 65536), "blockSize must be in (0, 65536]", new Object[] { Integer.valueOf(blockSize) });
  }
  
  protected void writeHeader(OutputStream out) throws IOException {
    out.write(SnappyFramed.HEADER_BYTES);
  }
  
  protected void writeBlock(OutputStream out, byte[] data, int offset, int length, boolean compressed, int crc32c) throws IOException {
    out.write(compressed ? 0 : 1);
    int headerLength = length + 4;
    out.write(headerLength);
    out.write(headerLength >>> 8);
    out.write(headerLength >>> 16);
    out.write(crc32c);
    out.write(crc32c >>> 8);
    out.write(crc32c >>> 16);
    out.write(crc32c >>> 24);
    out.write(data, offset, length);
  }
}
