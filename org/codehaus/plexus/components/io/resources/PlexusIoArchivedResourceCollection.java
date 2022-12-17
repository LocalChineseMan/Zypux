package org.codehaus.plexus.components.io.resources;

import java.io.File;

public interface PlexusIoArchivedResourceCollection extends PlexusIoResourceCollection {
  void setFile(File paramFile);
  
  File getFile();
}
