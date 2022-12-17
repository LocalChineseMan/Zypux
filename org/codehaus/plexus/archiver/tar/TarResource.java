package org.codehaus.plexus.archiver.tar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;

public class TarResource extends AbstractPlexusIoResource implements ResourceAttributeSupplier {
  private final TarFile tarFile;
  
  private final TarArchiveEntry entry;
  
  private PlexusIoResourceAttributes attributes;
  
  public TarResource(TarFile tarFile, TarArchiveEntry entry) {
    super(entry.getName(), getLastModifiedTime(entry), 
        entry.isDirectory() ? -1L : entry.getSize(), !entry.isDirectory(), entry
        .isDirectory(), true);
    this.tarFile = tarFile;
    this.entry = entry;
  }
  
  private static long getLastModifiedTime(TarArchiveEntry entry) {
    long l = entry.getModTime().getTime();
    return (l == -1L) ? 0L : l;
  }
  
  public synchronized PlexusIoResourceAttributes getAttributes() {
    if (this.attributes == null)
      this
        .attributes = (PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(this.entry.getUserId()), this.entry.getUserName(), Integer.valueOf(this.entry.getGroupId()), this.entry.getGroupName(), this.entry.getMode()); 
    return this.attributes;
  }
  
  public synchronized void setAttributes(PlexusIoResourceAttributes attributes) {
    this.attributes = attributes;
  }
  
  public URL getURL() throws IOException {
    return null;
  }
  
  @Nonnull
  public InputStream getContents() throws IOException {
    return this.tarFile.getInputStream(this.entry);
  }
}
