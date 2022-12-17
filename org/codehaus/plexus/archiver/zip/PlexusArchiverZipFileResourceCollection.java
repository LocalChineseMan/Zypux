package org.codehaus.plexus.archiver.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import javax.inject.Named;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

@Named("zip")
public class PlexusArchiverZipFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection implements EncodingSupported {
  private Charset charset = StandardCharsets.UTF_8;
  
  protected Iterator<PlexusIoResource> getEntries() throws IOException {
    File f = getFile();
    if (f == null)
      throw new IOException("The tar archive file has not been set."); 
    ZipFile zipFile = new ZipFile(f, (this.charset != null) ? this.charset.name() : "UTF8");
    return new CloseableIterator(zipFile);
  }
  
  public boolean isConcurrentAccessSupported() {
    return false;
  }
  
  class CloseableIterator implements Iterator<PlexusIoResource>, Closeable {
    final Enumeration<ZipArchiveEntry> en;
    
    private final ZipFile zipFile;
    
    public CloseableIterator(ZipFile zipFile) {
      this.en = zipFile.getEntriesInPhysicalOrder();
      this.zipFile = zipFile;
    }
    
    public boolean hasNext() {
      return this.en.hasMoreElements();
    }
    
    public PlexusIoResource next() {
      ZipArchiveEntry entry = this.en.nextElement();
      return entry.isUnixSymlink() ? 
        (PlexusIoResource)new ZipSymlinkResource(this.zipFile, entry, PlexusArchiverZipFileResourceCollection.this.getStreamTransformer()) : 
        (PlexusIoResource)new ZipResource(this.zipFile, entry, PlexusArchiverZipFileResourceCollection.this.getStreamTransformer());
    }
    
    public void remove() {
      throw new UnsupportedOperationException("Removing isn't implemented.");
    }
    
    public void close() throws IOException {
      this.zipFile.close();
    }
  }
  
  public void setEncoding(Charset charset) {
    this.charset = charset;
  }
}
