package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;

public class BZip2TarFile extends TarFile {
  public BZip2TarFile(File file) {
    super(file);
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return (InputStream)BZip2UnArchiver.getBZip2InputStream(super.getInputStream(file));
  }
}
