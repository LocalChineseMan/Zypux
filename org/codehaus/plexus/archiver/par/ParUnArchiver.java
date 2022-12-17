package org.codehaus.plexus.archiver.par;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("par")
public class ParUnArchiver extends ZipUnArchiver {
  public ParUnArchiver() {}
  
  public ParUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
