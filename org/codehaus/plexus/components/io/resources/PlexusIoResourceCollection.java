package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public interface PlexusIoResourceCollection extends Iterable<PlexusIoResource> {
  Iterator<PlexusIoResource> getResources() throws IOException;
  
  Stream stream();
  
  String getName(PlexusIoResource paramPlexusIoResource);
  
  long getLastModified() throws IOException;
  
  InputStream getInputStream(PlexusIoResource paramPlexusIoResource) throws IOException;
  
  PlexusIoResource resolve(PlexusIoResource paramPlexusIoResource) throws IOException;
  
  boolean isConcurrentAccessSupported();
}
