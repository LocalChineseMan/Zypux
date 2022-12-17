package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ZipEncoding {
  boolean canEncode(String paramString);
  
  ByteBuffer encode(String paramString) throws IOException;
  
  String decode(byte[] paramArrayOfbyte) throws IOException;
}
