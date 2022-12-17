package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class DeflateStream extends InflaterInputStream {
  private boolean closed = false;
  
  public DeflateStream(InputStream in, Inflater inflater) {
    super(in, inflater);
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    this.closed = true;
    this.inf.end();
    super.close();
  }
}
