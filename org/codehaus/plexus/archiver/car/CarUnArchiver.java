package org.codehaus.plexus.archiver.car;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("car")
public class CarUnArchiver extends ZipUnArchiver {
  public CarUnArchiver() {}
  
  public CarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
