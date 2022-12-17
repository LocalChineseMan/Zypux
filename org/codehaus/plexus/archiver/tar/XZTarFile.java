package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;

public class XZTarFile extends TarFile {
  public XZTarFile(File file) {
    super(file);
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return (InputStream)XZUnArchiver.getXZInputStream(super.getInputStream(file));
  }
}
