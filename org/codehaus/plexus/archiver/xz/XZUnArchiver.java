package org.codehaus.plexus.archiver.xz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.inject.Named;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;

@Named("xz")
public class XZUnArchiver extends AbstractUnArchiver {
  private static final String OPERATION_XZ = "xz";
  
  public XZUnArchiver() {}
  
  public XZUnArchiver(File source) {
    super(source);
  }
  
  protected void execute() throws ArchiverException {
    if (getSourceFile().lastModified() > getDestFile().lastModified()) {
      getLogger().info("Expanding " + getSourceFile().getAbsolutePath() + " to " + 
          getDestFile().getAbsolutePath());
      Streams.copyFully((InputStream)getXZInputStream(Streams.bufferedInputStream(Streams.fileInputStream(getSourceFile(), "xz"))), 
          Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile(), "xz")), "xz");
    } 
  }
  
  @Nonnull
  public static XZCompressorInputStream getXZInputStream(InputStream in) throws ArchiverException {
    try {
      return new XZCompressorInputStream(in);
    } catch (IOException ioe) {
      throw new ArchiverException("Trouble creating BZIP2 compressor, invalid file ?", ioe);
    } 
  }
  
  protected void execute(String path, File outputDirectory) throws ArchiverException {
    throw new UnsupportedOperationException("Targeted execution not supported in xz format");
  }
}
