package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class RangeEncoderToStream extends RangeEncoder {
  private final OutputStream out;
  
  public RangeEncoderToStream(OutputStream paramOutputStream) {
    this.out = paramOutputStream;
    reset();
  }
  
  void writeByte(int paramInt) throws IOException {
    this.out.write(paramInt);
  }
}
