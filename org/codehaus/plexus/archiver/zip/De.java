package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

public class DeferredScatterOutputStream implements ScatterGatherBackingStore {
  private final OffloadingOutputStream dfos;
  
  public DeferredScatterOutputStream(int threshold) {
    this.dfos = new OffloadingOutputStream(threshold, "scatterzipfragment", "zip", null);
  }
  
  public InputStream getInputStream() throws IOException {
    return this.dfos.getInputStream();
  }
  
  public void writeOut(byte[] data, int offset, int length) throws IOException {
    this.dfos.write(data, offset, length);
  }
  
  public void closeForWriting() throws IOException {
    this.dfos.close();
  }
  
  public void close() throws IOException {
    File file = this.dfos.getFile();
    if (file != null)
      file.delete(); 
  }
}
