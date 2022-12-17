package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;

public class SnappyFramedInputStream extends AbstractSnappyInputStream {
  public SnappyFramedInputStream(InputStream in, boolean verifyChecksums) throws IOException {
    super(in, 65536, 4, verifyChecksums, SnappyFramed.HEADER_BYTES);
  }
  
  protected AbstractSnappyInputStream.FrameMetaData getFrameMetaData(byte[] frameHeader) throws IOException {
    int minLength;
    AbstractSnappyInputStream.FrameAction frameAction;
    int length = frameHeader[1] & 0xFF;
    length |= (frameHeader[2] & 0xFF) << 8;
    length |= (frameHeader[3] & 0xFF) << 16;
    int flag = frameHeader[0] & 0xFF;
    switch (flag) {
      case 0:
        frameAction = AbstractSnappyInputStream.FrameAction.UNCOMPRESS;
        minLength = 5;
        break;
      case 1:
        frameAction = AbstractSnappyInputStream.FrameAction.RAW;
        minLength = 5;
        break;
      case 255:
        if (length != 6)
          throw new IOException("stream identifier chunk with invalid length: " + length); 
        frameAction = AbstractSnappyInputStream.FrameAction.SKIP;
        minLength = 6;
        break;
      default:
        if (flag <= 127)
          throw new IOException("unsupported unskippable chunk: " + Integer.toHexString(flag)); 
        frameAction = AbstractSnappyInputStream.FrameAction.SKIP;
        minLength = 0;
        break;
    } 
    if (length < minLength)
      throw new IOException("invalid length: " + length + " for chunk flag: " + Integer.toHexString(flag)); 
    return new AbstractSnappyInputStream.FrameMetaData(frameAction, length);
  }
  
  protected AbstractSnappyInputStream.FrameData getFrameData(byte[] frameHeader, byte[] content, int length) {
    int crc32c = (content[3] & 0xFF) << 24 | (content[2] & 0xFF) << 16 | (content[1] & 0xFF) << 8 | content[0] & 0xFF;
    return new AbstractSnappyInputStream.FrameData(crc32c, 4);
  }
}
