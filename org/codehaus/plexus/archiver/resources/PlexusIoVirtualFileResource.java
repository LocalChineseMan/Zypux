package org.codehaus.plexus.archiver.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.attributes.AttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;

public class PlexusIoVirtualFileResource extends AbstractPlexusIoResource implements ResourceAttributeSupplier {
  private final File file;
  
  protected PlexusIoVirtualFileResource(File file, String name) {
    super(name, file.lastModified(), file.length(), file.isFile(), file.isDirectory(), file.exists());
    this.file = file;
  }
  
  protected static String getName(File file) {
    return file.getPath().replace('\\', '/');
  }
  
  public File getFile() {
    return this.file;
  }
  
  @Nonnull
  public InputStream getContents() throws IOException {
    throw new UnsupportedOperationException("We're not really sure we can do this");
  }
  
  public URL getURL() throws IOException {
    return getFile().toURI().toURL();
  }
  
  public long getSize() {
    return getFile().length();
  }
  
  public boolean isDirectory() {
    return getFile().isDirectory();
  }
  
  public boolean isExisting() {
    return getFile().exists();
  }
  
  public boolean isFile() {
    return getFile().isFile();
  }
  
  public PlexusIoResourceAttributes getAttributes() {
    return null;
  }
  
  public long getLastModified() {
    if (this.file.exists())
      return AttributeUtils.getLastModified(getFile()); 
    return System.currentTimeMillis();
  }
  
  public boolean isSymbolicLink() {
    return getAttributes().isSymbolicLink();
  }
}
