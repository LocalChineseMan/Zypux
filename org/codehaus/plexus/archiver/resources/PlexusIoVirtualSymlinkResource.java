package org.codehaus.plexus.archiver.resources;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

public class PlexusIoVirtualSymlinkResource extends PlexusIoVirtualFileResource implements SymlinkDestinationSupplier {
  private final String symnlinkDestination;
  
  public PlexusIoVirtualSymlinkResource(File symlinkFile, String symnlinkDestination) {
    super(symlinkFile, getName(symlinkFile));
    this.symnlinkDestination = symnlinkDestination;
  }
  
  public String getSymlinkDestination() throws IOException {
    return (this.symnlinkDestination == null) ? 
      SymlinkUtils.readSymbolicLink(getFile()).toString() : 
      this.symnlinkDestination;
  }
  
  public boolean isSymbolicLink() {
    return true;
  }
}
