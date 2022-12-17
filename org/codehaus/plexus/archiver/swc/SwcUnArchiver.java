package org.codehaus.plexus.archiver.swc;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("swc")
public class SwcUnArchiver extends ZipUnArchiver {
  public SwcUnArchiver() {}
  
  public SwcUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
