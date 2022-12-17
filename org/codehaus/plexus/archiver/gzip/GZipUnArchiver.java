package org.codehaus.plexus.archiver.gzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.inject.Named;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;

@Named("gzip")
public class GZipUnArchiver extends AbstractUnArchiver {
  private static final String OPERATION_GZIP = "gzip";
  
  public GZipUnArchiver() {}
  
  public GZipUnArchiver(File sourceFile) {
    super(sourceFile);
  }
  
  protected void execute() throws ArchiverException {
    if (getSourceFile().lastModified() > getDestFile().lastModified()) {
      getLogger().info("Expanding " + getSourceFile().getAbsolutePath() + " to " + 
          getDestFile().getAbsolutePath());
      Streams.copyFully(getGzipInputStream(Streams.fileInputStream(getSourceFile(), "gzip")), 
          Streams.fileOutputStream(getDestFile(), "gzip"), "gzip");
    } 
  }
  
  private InputStream getGzipInputStream(InputStream in) throws ArchiverException {
    try {
      return Streams.bufferedInputStream(new GZIPInputStream(in));
    } catch (IOException e) {
      throw new ArchiverException("Problem creating GZIP input stream", e);
    } 
  }
  
  protected void execute(String path, File outputDirectory) {
    throw new UnsupportedOperationException("Targeted extraction not supported in GZIP format.");
  }
}
