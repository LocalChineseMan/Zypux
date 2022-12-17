package org.codehaus.plexus.archiver.ear;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("ear")
public class EarUnArchiver extends ZipUnArchiver {
  public EarUnArchiver() {}
  
  public EarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
