package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.snappy")
public class TarSnappyUnArchiver extends TarUnArchiver {
  public TarSnappyUnArchiver() {
    setupCompressionMethod();
  }
  
  public TarSnappyUnArchiver(File sourceFile) {
    super(sourceFile);
    setupCompressionMethod();
  }
  
  private void setupCompressionMethod() {
    setCompression(TarUnArchiver.UntarCompressionMethod.SNAPPY);
  }
}
