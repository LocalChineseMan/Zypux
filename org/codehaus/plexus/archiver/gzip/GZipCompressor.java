package org.codehaus.plexus.archiver.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.util.Streams;

public class GZipCompressor extends Compressor {
  private OutputStream zOut;
  
  public void compress() throws ArchiverException {
    try {
      this.zOut = Streams.bufferedOutputStream(new GZIPOutputStream(Streams.fileOutputStream(getDestFile())));
      compress(getSource(), this.zOut);
    } catch (IOException ioe) {
      String msg = "Problem creating gzip " + ioe.getMessage();
      throw new ArchiverException(msg, ioe);
    } 
  }
  
  public void close() {
    try {
      if (this.zOut != null) {
        this.zOut.close();
        this.zOut = null;
      } 
    } catch (IOException e) {
      throw new ArchiverException("Failure closing target.", e);
    } 
  }
}
