package org.codehaus.plexus.archiver.tar;

import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

public class TarSymlinkResource extends TarResource implements SymlinkDestinationSupplier {
  private final String symlinkDestination;
  
  public TarSymlinkResource(TarFile tarFile, TarArchiveEntry entry) {
    super(tarFile, entry);
    this.symlinkDestination = entry.getLinkName();
  }
  
  public String getSymlinkDestination() throws IOException {
    return this.symlinkDestination;
  }
  
  public boolean isSymbolicLink() {
    return true;
  }
}
