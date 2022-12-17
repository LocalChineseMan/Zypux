package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.annotation.Nonnull;

public abstract class PlexusIoURLResource extends AbstractPlexusIoResource {
  protected PlexusIoURLResource(@Nonnull String name, long lastModified, long size, boolean isFile, boolean isDirectory, boolean isExisting) {
    super(name, lastModified, size, isFile, isDirectory, isExisting);
  }
  
  @Nonnull
  public InputStream getContents() throws IOException {
    URL url = getURL();
    try {
      URLConnection uc = url.openConnection();
      uc.setUseCaches(false);
      return uc.getInputStream();
    } catch (IOException e) {
      throw new IOException(getDescriptionForError(url), e);
    } 
  }
  
  public String getDescriptionForError(URL url) {
    return (url != null) ? url.toExternalForm() : "url=null";
  }
  
  public abstract URL getURL() throws IOException;
}
