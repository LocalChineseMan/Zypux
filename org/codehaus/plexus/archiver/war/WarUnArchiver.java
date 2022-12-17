package org.codehaus.plexus.archiver.war;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("war")
public class WarUnArchiver extends ZipUnArchiver {
  public WarUnArchiver() {}
  
  public WarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
