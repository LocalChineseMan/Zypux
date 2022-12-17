package com.google.common.io;

import java.io.ByteArrayOutputStream;

class MemoryOutput extends ByteArrayOutputStream {
  private MemoryOutput() {}
  
  byte[] getBuffer() {
    return this.buf;
  }
  
  int getCount() {
    return this.count;
  }
}
