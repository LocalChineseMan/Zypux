package org.codehaus.plexus.archiver.zip;

import java.io.File;
import javax.inject.Named;

@Named("zip")
public class ZipUnArchiver extends AbstractZipUnArchiver {
  public ZipUnArchiver() {}
  
  public ZipUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
