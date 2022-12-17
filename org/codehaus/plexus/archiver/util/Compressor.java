package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Compressor {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  private File destFile;
  
  private PlexusIoResource source;
  
  protected Logger getLogger() {
    return this.logger;
  }
  
  public void setDestFile(File compressFile) {
    this.destFile = compressFile;
  }
  
  public File getDestFile() {
    return this.destFile;
  }
  
  public void setSource(PlexusIoResource source) {
    this.source = source;
  }
  
  public PlexusIoResource getSource() {
    return this.source;
  }
  
  private void compressFile(InputStream in, OutputStream zOut) throws IOException {
    byte[] buffer = new byte[8192];
    int count = 0;
    do {
      zOut.write(buffer, 0, count);
      count = in.read(buffer, 0, buffer.length);
    } while (count != -1);
  }
  
  protected void compress(PlexusIoResource resource, OutputStream zOut) throws IOException {
    InputStream in = Streams.bufferedInputStream(resource.getContents());
    try {
      compressFile(in, zOut);
      if (in != null)
        in.close(); 
    } catch (Throwable throwable) {
      if (in != null)
        try {
          in.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public abstract void compress() throws ArchiverException;
  
  public abstract void close() throws ArchiverException;
}
