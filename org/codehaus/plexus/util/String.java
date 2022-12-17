package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.OutputStream;

@Deprecated
public class StringOutputStream extends OutputStream {
  private StringBuffer buf = new StringBuffer();
  
  public void write(byte[] b) throws IOException {
    this.buf.append(new java.lang.String(b));
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    this.buf.append(new java.lang.String(b, off, len));
  }
  
  public void write(int b) throws IOException {
    byte[] bytes = new byte[1];
    bytes[0] = (byte)b;
    this.buf.append(new java.lang.String(bytes));
  }
  
  public java.lang.String toString() {
    return this.buf.toString();
  }
}
