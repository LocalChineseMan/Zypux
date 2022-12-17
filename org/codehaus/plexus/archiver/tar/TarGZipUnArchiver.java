package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.gz")
public class TarGZipUnArchiver extends TarUnArchiver {
  public TarGZipUnArchiver() {
    setupCompressionMethod();
  }
  
  public TarGZipUnArchiver(File sourceFile) {
    super(sourceFile);
    setupCompressionMethod();
  }
  
  private void setupCompressionMethod() {
    setCompression(TarUnArchiver.UntarCompressionMethod.GZIP);
  }
}
