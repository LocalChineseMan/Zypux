package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

public class ZipSymlinkResource extends ZipResource implements SymlinkDestinationSupplier {
  private final String symlinkDestination;
  
  public ZipSymlinkResource(ZipFile zipFile, ZipArchiveEntry entry, InputStreamTransformer streamTransformer) {
    super(zipFile, entry, streamTransformer);
    try {
      this.symlinkDestination = zipFile.getUnixSymlink(entry);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public String getSymlinkDestination() throws IOException {
    return this.symlinkDestination;
  }
  
  public boolean isSymbolicLink() {
    return true;
  }
}
