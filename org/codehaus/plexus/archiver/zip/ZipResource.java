package org.codehaus.plexus.archiver.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.annotation.Nonnull;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResource;
import org.codehaus.plexus.components.io.resources.ClosingInputStream;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class ZipResource extends AbstractPlexusIoResource implements ResourceAttributeSupplier {
  private final ZipFile zipFile;
  
  private final ZipArchiveEntry entry;
  
  private final InputStreamTransformer streamTransformer;
  
  private PlexusIoResourceAttributes attributes;
  
  public ZipResource(ZipFile zipFile, ZipArchiveEntry entry, InputStreamTransformer streamTransformer) {
    super(entry.getName(), getLastModified(entry), 
        entry.isDirectory() ? -1L : entry.getSize(), !entry.isDirectory(), entry
        .isDirectory(), true);
    this.zipFile = zipFile;
    this.entry = entry;
    this.streamTransformer = streamTransformer;
  }
  
  private static long getLastModified(ZipArchiveEntry entry) {
    long time = entry.getTime();
    return (time == -1L) ? 0L : time;
  }
  
  public synchronized PlexusIoResourceAttributes getAttributes() {
    int mode = -1;
    if (this.entry.getPlatform() == 3) {
      mode = this.entry.getUnixMode();
      if ((mode & 0x8000) == 32768) {
        mode &= 0xFFFF7FFF;
      } else {
        mode &= 0xFFFFBFFF;
      } 
    } 
    if (this.attributes == null)
      this.attributes = (PlexusIoResourceAttributes)new SimpleResourceAttributes(null, null, null, null, mode); 
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
    InputStream inputStream = this.zipFile.getInputStream(this.entry);
    return (InputStream)new ClosingInputStream(this.streamTransformer.transform((PlexusIoResource)this, inputStream), inputStream);
  }
}
