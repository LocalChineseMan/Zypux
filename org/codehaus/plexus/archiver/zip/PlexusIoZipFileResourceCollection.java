package org.codehaus.plexus.archiver.zip;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoArchiveResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoURLResource;

public class PlexusIoZipFileResourceCollection extends AbstractPlexusIoArchiveResourceCollection implements EncodingSupported {
  private Charset charset = StandardCharsets.UTF_8;
  
  public boolean isConcurrentAccessSupported() {
    return false;
  }
  
  protected Iterator<PlexusIoResource> getEntries() throws IOException {
    File f = getFile();
    if (f == null)
      throw new IOException("The zip file has not been set."); 
    URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { f.toURI().toURL() }, null) {
        public URL getResource(String name) {
          return findResource(name);
        }
      };
    URL url = new URL("jar:" + f.toURI().toURL() + "!/");
    JarFile jarFile = new JarFile(f);
    ZipFile zipFile = new ZipFile(f, (this.charset != null) ? this.charset.name() : "UTF8");
    Enumeration<ZipArchiveEntry> en = zipFile.getEntriesInPhysicalOrder();
    return new ZipFileResourceIterator(en, url, jarFile, zipFile, urlClassLoader);
  }
  
  private static class ZipFileResourceIterator implements Iterator<PlexusIoResource>, Closeable {
    private final Enumeration<ZipArchiveEntry> en;
    
    private final URL url;
    
    private final JarFile jarFile;
    
    private final ZipFile zipFile;
    
    private final URLClassLoader urlClassLoader;
    
    private class ZipFileResource extends PlexusIoURLResource {
      private final JarFile jarFile;
      
      private ZipFileResource(JarFile jarFile, ZipArchiveEntry entry) {
        super(entry.getName(), 
            (entry.getTime() == -1L) ? 0L : entry.getTime(), 
            entry.isDirectory() ? -1L : entry.getSize(), 
            !entry.isDirectory(), entry.isDirectory(), true);
        this.jarFile = jarFile;
      }
      
      public InputStream getContents() throws IOException {
        return this.jarFile.getInputStream(this.jarFile.getEntry(getName()));
      }
      
      public URL getURL() throws IOException {
        String spec = getName();
        if (spec.startsWith("/")) {
          spec = "./" + spec;
          return new URL(PlexusIoZipFileResourceCollection.ZipFileResourceIterator.this.url, spec);
        } 
        return PlexusIoZipFileResourceCollection.ZipFileResourceIterator.this.urlClassLoader.getResource(spec);
      }
    }
    
    private class ZipFileSymlinkResource extends ZipFileResource implements SymlinkDestinationSupplier {
      private final ZipArchiveEntry entry;
      
      private ZipFileSymlinkResource(JarFile jarFile, ZipArchiveEntry entry) {
        super(jarFile, entry);
        this.entry = entry;
      }
      
      public String getSymlinkDestination() throws IOException {
        return PlexusIoZipFileResourceCollection.ZipFileResourceIterator.this.zipFile.getUnixSymlink(this.entry);
      }
      
      public boolean isSymbolicLink() {
        return true;
      }
    }
    
    public ZipFileResourceIterator(Enumeration<ZipArchiveEntry> en, URL url, JarFile jarFile, ZipFile zipFile, URLClassLoader urlClassLoader) {
      this.en = en;
      this.url = url;
      this.jarFile = jarFile;
      this.zipFile = zipFile;
      this.urlClassLoader = urlClassLoader;
    }
    
    public boolean hasNext() {
      return this.en.hasMoreElements();
    }
    
    public PlexusIoResource next() {
      ZipArchiveEntry entry = this.en.nextElement();
      return entry.isUnixSymlink() ? 
        (PlexusIoResource)new ZipFileSymlinkResource(this.jarFile, entry) : 
        (PlexusIoResource)new ZipFileResource(this.jarFile, entry);
    }
    
    public void remove() {
      throw new UnsupportedOperationException("Removing isn't implemented.");
    }
    
    public void close() throws IOException {
      try {
        this.urlClassLoader.close();
      } finally {
        try {
          this.zipFile.close();
        } finally {
          this.jarFile.close();
        } 
      } 
    }
  }
  
  public void setEncoding(Charset charset) {
    this.charset = charset;
  }
}
