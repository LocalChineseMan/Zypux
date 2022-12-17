package org.iq80.snappy;

import java.io.IOException;
import java.io.OutputStream;

@Deprecated
public class SnappyOutputStream extends AbstractSnappyOutputStream {
  static final byte[] STREAM_HEADER = new byte[] { 115, 110, 97, 112, 112, 121, 0 };
  
  static final int MAX_BLOCK_SIZE = 32768;
  
  public static final double MIN_COMPRESSION_RATIO = 0.875D;
  
  private final boolean calculateChecksum;
  
  public SnappyOutputStream(OutputStream out) throws IOException {
    this(out, true);
  }
  
  private SnappyOutputStream(OutputStream out, boolean calculateChecksum) throws IOException {
    super(out, 32768, 0.875D);
    this.calculateChecksum = calculateChecksum;
  }
  
  public static SnappyOutputStream newChecksumFreeBenchmarkOutputStream(OutputStream out) throws IOException {
    return new SnappyOutputStream(out, false);
  }
  
  protected void writeHeader(OutputStream out) throws IOException {
    out.write(STREAM_HEADER);
  }
  
  protected int calculateCRC32C(byte[] data, int offset, int length) {
    return this.calculateChecksum ? super.calculateCRC32C(data, offset, length) : 0;
  }
  
  protected void writeBlock(OutputStream out, byte[] data, int offset, int length, boolean compressed, int crc32c) throws IOException {
    out.write(compressed ? 1 : 0);
    out.write(length >>> 8);
    out.write(length);
    out.write(crc32c >>> 24);
    out.write(crc32c >>> 16);
    out.write(crc32c >>> 8);
    out.write(crc32c);
    out.write(data, offset, length);
  }
}
