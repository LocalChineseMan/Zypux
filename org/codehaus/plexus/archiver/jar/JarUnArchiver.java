package org.codehaus.plexus.archiver.jar;

import java.io.File;
import javax.inject.Named;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

@Named("jar")
public class JarUnArchiver extends ZipUnArchiver {
  public JarUnArchiver() {}
  
  public JarUnArchiver(File sourceFile) {
    super(sourceFile);
  }
}
