package org.codehaus.plexus.archiver.bzip2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.inject.Named;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;

@Named("bzip2")
public class BZip2UnArchiver extends AbstractUnArchiver {
  private static final String OPERATION_BZIP2 = "bzip2";
  
  public BZip2UnArchiver() {}
  
  public BZip2UnArchiver(File sourceFile) {
    super(sourceFile);
  }
  
  protected void execute() throws ArchiverException {
    if (getSourceFile().lastModified() > getDestFile().lastModified()) {
      getLogger().info("Expanding " + 
          getSourceFile().getAbsolutePath() + " to " + getDestFile().getAbsolutePath());
      Streams.copyFully((InputStream)getBZip2InputStream(Streams.bufferedInputStream(Streams.fileInputStream(getSourceFile(), "bzip2"))), 
          Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile(), "bzip2")), "bzip2");
    } 
  }
  
  @Nonnull
  public static BZip2CompressorInputStream getBZip2InputStream(InputStream bis) throws ArchiverException {
    try {
      return new BZip2CompressorInputStream(bis);
    } catch (IOException e) {
      throw new ArchiverException("Trouble creating BZIP2 compressor, invalid file ?", e);
    } 
  }
  
  protected void execute(String path, File outputDirectory) {
    throw new UnsupportedOperationException("Targeted extraction not supported in BZIP2 format.");
  }
}
