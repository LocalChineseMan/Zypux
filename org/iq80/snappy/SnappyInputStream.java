package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Deprecated
public class SnappyInputStream extends AbstractSnappyInputStream {
  private static final int HEADER_LENGTH = 7;
  
  public SnappyInputStream(InputStream in) throws IOException {
    this(in, true);
  }
  
  public SnappyInputStream(InputStream in, boolean verifyChecksums) throws IOException {
    super(in, 32768, 7, verifyChecksums, SnappyOutputStream.STREAM_HEADER);
  }
  
  protected AbstractSnappyInputStream.FrameMetaData getFrameMetaData(byte[] frameHeader) throws IOException {
    AbstractSnappyInputStream.FrameAction action;
    int x = frameHeader[0] & 0xFF;
    int a = frameHeader[1] & 0xFF;
    int b = frameHeader[2] & 0xFF;
    int length = a << 8 | b;
    switch (x) {
      case 0:
        action = AbstractSnappyInputStream.FrameAction.RAW;
        break;
      case 1:
        action = AbstractSnappyInputStream.FrameAction.UNCOMPRESS;
        break;
      case 115:
        if (!Arrays.equals(SnappyOutputStream.STREAM_HEADER, frameHeader))
          throw new IOException(String.format("invalid compressed flag in header: 0x%02x", new Object[] { Integer.valueOf(x) })); 
        action = AbstractSnappyInputStream.FrameAction.SKIP;
        length = 0;
        break;
      default:
        throw new IOException(String.format("invalid compressed flag in header: 0x%02x", new Object[] { Integer.valueOf(x) }));
    } 
    if ((length <= 0 || length > 32768) && action != AbstractSnappyInputStream.FrameAction.SKIP)
      throw new IOException("invalid block size in header: " + length); 
    return new AbstractSnappyInputStream.FrameMetaData(action, length);
  }
  
  protected AbstractSnappyInputStream.FrameData getFrameData(byte[] frameHeader, byte[] content, int length) {
    int crc32c = (frameHeader[3] & 0xFF) << 24 | (frameHeader[4] & 0xFF) << 16 | (frameHeader[5] & 0xFF) << 8 | frameHeader[6] & 0xFF;
    return new AbstractSnappyInputStream.FrameData(crc32c, 0);
  }
}
