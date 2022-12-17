package org.tukaani.xz;

import java.io.InputStream;

class DeltaDecoder extends DeltaCoder implements FilterDecoder {
  private final int distance;
  
  DeltaDecoder(byte[] paramArrayOfbyte) throws UnsupportedOptionsException {
    if (paramArrayOfbyte.length != 1)
      throw new UnsupportedOptionsException("Unsupported Delta filter properties"); 
    this.distance = (paramArrayOfbyte[0] & 0xFF) + 1;
  }
  
  public int getMemoryUsage() {
    return 1;
  }
  
  public InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) {
    return new DeltaInputStream(paramInputStream, this.distance);
  }
}
