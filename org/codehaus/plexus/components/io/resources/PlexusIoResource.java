package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.SizeSupplier;

public interface PlexusIoResource extends FileInfo, SizeSupplier, ContentSupplier {
  public static final long UNKNOWN_RESOURCE_SIZE = -1L;
  
  public static final long UNKNOWN_MODIFICATION_DATE = 0L;
  
  long getLastModified();
  
  boolean isExisting();
  
  long getSize();
  
  boolean isFile();
  
  boolean isDirectory();
  
  @Nonnull
  InputStream getContents() throws IOException;
  
  URL getURL() throws IOException;
}
