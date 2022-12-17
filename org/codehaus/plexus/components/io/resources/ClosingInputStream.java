package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import java.io.InputStream;

public class ClosingInputStream extends InputStream {
  private final InputStream target;
  
  private final InputStream other;
  
  public ClosingInputStream(InputStream target, InputStream other) {
    this.target = target;
    this.other = other;
  }
  
  public int read() throws IOException {
    return this.target.read();
  }
  
  public int read(byte[] b) throws IOException {
    return this.target.read(b);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    return this.target.read(b, off, len);
  }
  
  public long skip(long n) throws IOException {
    return this.target.skip(n);
  }
  
  public int available() throws IOException {
    return this.target.available();
  }
  
  public void close() throws IOException {
    this.other.close();
    this.target.close();
  }
  
  public void mark(int readlimit) {
    this.target.mark(readlimit);
  }
  
  public void reset() throws IOException {
    this.target.reset();
  }
  
  public boolean markSupported() {
    return this.target.markSupported();
  }
}
