package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.bz2")
public class TarBZip2UnArchiver extends TarUnArchiver {
  public TarBZip2UnArchiver() {
    setupCompressionMethod();
  }
  
  public TarBZip2UnArchiver(File sourceFile) {
    super(sourceFile);
    setupCompressionMethod();
  }
  
  private void setupCompressionMethod() {
    setCompression(TarUnArchiver.UntarCompressionMethod.BZIP2);
  }
}
