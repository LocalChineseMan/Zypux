package org.codehaus.plexus.archiver.snappy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.inject.Named;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;
import org.iq80.snappy.SnappyFramedInputStream;

@Named("snappy")
public class SnappyUnArchiver extends AbstractUnArchiver {
  private static final String OPERATION_SNAPPY = "snappy";
  
  public SnappyUnArchiver() {}
  
  public SnappyUnArchiver(File sourceFile) {
    super(sourceFile);
  }
  
  protected void execute() throws ArchiverException {
    if (getSourceFile().lastModified() > getDestFile().lastModified()) {
      getLogger().info("Expanding " + 
          getSourceFile().getAbsolutePath() + " to " + getDestFile().getAbsolutePath());
      Streams.copyFully(
          (InputStream)getSnappyInputStream(Streams.bufferedInputStream(Streams.fileInputStream(getSourceFile(), "snappy"))), 
          Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile(), "snappy")), "snappy");
    } 
  }
  
  @Nonnull
  public static SnappyFramedInputStream getSnappyInputStream(InputStream bis) throws ArchiverException {
    try {
      return new SnappyFramedInputStream(bis, true);
    } catch (IOException e) {
      throw new ArchiverException("Trouble creating Snappy compressor, invalid file ?", e);
    } 
  }
  
  protected void execute(String path, File outputDirectory) {
    throw new UnsupportedOperationException("Targeted extraction not supported in Snappy format.");
  }
}
