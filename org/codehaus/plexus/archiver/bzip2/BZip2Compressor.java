package org.codehaus.plexus.archiver.bzip2;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.util.Streams;

public class BZip2Compressor extends Compressor {
  private BZip2CompressorOutputStream zOut;
  
  public void compress() throws ArchiverException {
    try {
      this.zOut = new BZip2CompressorOutputStream(Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile())));
      compress(getSource(), (OutputStream)this.zOut);
    } catch (IOException ioe) {
      String msg = "Problem creating bzip2 " + ioe.getMessage();
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
