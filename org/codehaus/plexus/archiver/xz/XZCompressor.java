package org.codehaus.plexus.archiver.xz;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Compressor;
import org.codehaus.plexus.archiver.util.Streams;

public class XZCompressor extends Compressor {
  private XZCompressorOutputStream xzOut;
  
  public void compress() throws ArchiverException {
    try {
      this.xzOut = new XZCompressorOutputStream(Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile())));
      compress(getSource(), (OutputStream)this.xzOut);
    } catch (IOException ioe) {
      throw new ArchiverException("Problem creating xz " + ioe.getMessage(), ioe);
    } 
  }
  
  public void close() {
    try {
      if (this.xzOut != null) {
        this.xzOut.close();
        this.xzOut = null;
      } 
    } catch (IOException e) {
      throw new ArchiverException("Failure closing target.", e);
    } 
  }
}
