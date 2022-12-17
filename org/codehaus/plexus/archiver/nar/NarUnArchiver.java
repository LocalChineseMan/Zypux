package org.codehaus.plexus.archiver.nar;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("nar")
public class NarUnArchiver extends ZipUnArchiver {
  public NarUnArchiver() {}
  
  public NarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
