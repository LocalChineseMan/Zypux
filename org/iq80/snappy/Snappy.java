package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class Snappy {
  private static final int MAX_HEADER_LENGTH = Math.max(SnappyOutputStream.STREAM_HEADER.length, SnappyFramed.HEADER_BYTES.length);
  
  static final int LITERAL = 0;
  
  static final int COPY_1_BYTE_OFFSET = 1;
  
  static final int COPY_2_BYTE_OFFSET = 2;
  
  static final int COPY_4_BYTE_OFFSET = 3;
  
  public static InputStream determineSnappyInputStream(InputStream source, boolean verifyChecksums) throws IOException {
    SnappyInternalUtils.checkNotNull(source, "source is null", new Object[0]);
    SnappyInternalUtils.checkArgument(source.markSupported(), "source does not support mark/reset", new Object[0]);
    source.mark(MAX_HEADER_LENGTH);
    byte[] buffer = new byte[MAX_HEADER_LENGTH];
    int read = SnappyInternalUtils.readBytes(source, buffer, 0, MAX_HEADER_LENGTH);
    source.reset();
    if (read != SnappyOutputStream.STREAM_HEADER.length || read != SnappyFramed.HEADER_BYTES.length)
      throw new IllegalArgumentException("invalid header"); 
    if (buffer[0] == SnappyFramed.HEADER_BYTES[0]) {
      SnappyInternalUtils.checkArgument(Arrays.equals(Arrays.copyOf(buffer, SnappyFramed.HEADER_BYTES.length), SnappyFramed.HEADER_BYTES), "invalid header", new Object[0]);
      return new SnappyFramedInputStream(source, verifyChecksums);
    } 
    SnappyInternalUtils.checkArgument(Arrays.equals(Arrays.copyOf(buffer, SnappyOutputStream.STREAM_HEADER.length), SnappyOutputStream.STREAM_HEADER), "invalid header", new Object[0]);
    return new SnappyInputStream(source, verifyChecksums);
  }
  
  public static int getUncompressedLength(byte[] compressed, int compressedOffset) throws CorruptionException {
    return SnappyDecompressor.getUncompressedLength(compressed, compressedOffset);
  }
  
  public static byte[] uncompress(byte[] compressed, int compressedOffset, int compressedSize) throws CorruptionException {
    return SnappyDecompressor.uncompress(compressed, compressedOffset, compressedSize);
  }
  
  public static int uncompress(byte[] compressed, int compressedOffset, int compressedSize, byte[] uncompressed, int uncompressedOffset) throws CorruptionException {
    return SnappyDecompressor.uncompress(compressed, compressedOffset, compressedSize, uncompressed, uncompressedOffset);
  }
  
  public static int maxCompressedLength(int sourceLength) {
    return SnappyCompressor.maxCompressedLength(sourceLength);
  }
  
  public static int compress(byte[] uncompressed, int uncompressedOffset, int uncompressedLength, byte[] compressed, int compressedOffset) {
    return SnappyCompressor.compress(uncompressed, uncompressedOffset, uncompressedLength, compressed, compressedOffset);
  }
  
  public static byte[] compress(byte[] data) {
    byte[] compressedOut = new byte[maxCompressedLength(data.length)];
    int compressedSize = compress(data, 0, data.length, compressedOut, 0);
    byte[] trimmedBuffer = Arrays.copyOf(compressedOut, compressedSize);
    return trimmedBuffer;
  }
}
