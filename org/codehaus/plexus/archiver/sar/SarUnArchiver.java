package org.codehaus.plexus.archiver.sar;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("sar")
public class SarUnArchiver extends ZipUnArchiver {
  public SarUnArchiver() {}
  
  public SarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
