package org.codehaus.plexus.archiver.rar;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("rar")
public class RarUnArchiver extends ZipUnArchiver {
  public RarUnArchiver() {}
  
  public RarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
