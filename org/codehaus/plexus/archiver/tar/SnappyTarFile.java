package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;

public class SnappyTarFile extends TarFile {
  public SnappyTarFile(File file) {
    super(file);
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return (InputStream)SnappyUnArchiver.getSnappyInputStream(super.getInputStream(file));
  }
}
