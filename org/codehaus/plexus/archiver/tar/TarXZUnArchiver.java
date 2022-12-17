package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.xz")
public class TarXZUnArchiver extends TarUnArchiver {
  public TarXZUnArchiver() {
    setupCompressionMethod();
  }
  
  public TarXZUnArchiver(File sourceFile) {
    super(sourceFile);
    setupCompressionMethod();
  }
  
  private void setupCompressionMethod() {
    setCompression(TarUnArchiver.UntarCompressionMethod.XZ);
  }
}
