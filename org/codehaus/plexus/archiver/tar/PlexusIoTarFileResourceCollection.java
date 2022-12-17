package org.codehaus.plexus.archiver.tar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import javax.inject.Named;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

@Named("tar")
public class PlexusIoTarFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection implements Closeable {
  protected TarFile newTarFile(File file) {
    return new TarFile(file);
  }
  
  TarFile tarFile = null;
  
  public void close() throws IOException {
    if (this.tarFile != null)
      this.tarFile.close(); 
  }
  
  public boolean isConcurrentAccessSupported() {
    return false;
  }
  
  protected Iterator<PlexusIoResource> getEntries() throws IOException {
    File f = getFile();
    if (f == null)
      throw new IOException("The tar archive file has not been set."); 
    if (this.tarFile == null)
      this.tarFile = newTarFile(f); 
    final Enumeration<ArchiveEntry> en = this.tarFile.getEntries();
    return new Iterator<PlexusIoResource>() {
        public boolean hasNext() {
          return en.hasMoreElements();
        }
        
        public PlexusIoResource next() {
          TarArchiveEntry entry = en.nextElement();
          return entry.isSymbolicLink() ? 
            (PlexusIoResource)new TarSymlinkResource(PlexusIoTarFileResourceCollection.this.tarFile, entry) : 
            (PlexusIoResource)new TarResource(PlexusIoTarFileResourceCollection.this.tarFile, entry);
        }
        
        public void remove() {
          throw new UnsupportedOperationException("Removing isn't implemented.");
        }
      };
  }
}
