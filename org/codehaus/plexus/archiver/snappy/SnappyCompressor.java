package org.codehaus.plexus.archiver.snappy;

import java.io.IOException;
import java.io.OutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.util.Streams;
import org.iq80.snappy.SnappyFramedOutputStream;

public class SnappyCompressor extends Compressor {
  private SnappyFramedOutputStream zOut;
  
  public void compress() throws ArchiverException {
    try {
      this.zOut = new SnappyFramedOutputStream(Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile())));
      compress(getSource(), (OutputStream)this.zOut);
    } catch (IOException ioe) {
      String msg = "Problem creating snappy " + ioe.getMessage();
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
