package org.codehaus.plexus.archiver.esb;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("esb")
public class EsbUnArchiver extends ZipUnArchiver {
  public EsbUnArchiver() {}
  
  public EsbUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
