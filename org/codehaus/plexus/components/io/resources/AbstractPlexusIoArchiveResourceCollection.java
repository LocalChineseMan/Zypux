package org.codehaus.plexus.components.io.resources;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.codehaus.plexus.components.io.functions.PlexusIoResourceConsumer;

public abstract class AbstractPlexusIoArchiveResourceCollection extends AbstractPlexusIoResourceCollection implements PlexusIoArchivedResourceCollection {
  private File file;
  
  public void setFile(File file) {
    this.file = file;
  }
  
  public File getFile() {
    return this.file;
  }
  
  protected abstract Iterator<PlexusIoResource> getEntries() throws IOException;
  
  public Iterator<PlexusIoResource> getResources() throws IOException {
    return new FilteringIterator();
  }
  
  class FilteringIterator implements Iterator<PlexusIoResource>, Closeable {
    final Iterator<PlexusIoResource> it = AbstractPlexusIoArchiveResourceCollection.this.getEntries();
    
    PlexusIoResource next;
    
    boolean doNext() {
      while (this.it.hasNext()) {
        PlexusIoResource candidate = this.it.next();
        try {
          if (AbstractPlexusIoArchiveResourceCollection.this.isSelected(candidate)) {
            this.next = candidate;
            return true;
          } 
        } catch (IOException e) {
          throw new RuntimeException(e);
        } 
      } 
      return false;
    }
    
    public boolean hasNext() {
      return doNext();
    }
    
    public PlexusIoResource next() {
      if (this.next == null)
        doNext(); 
      PlexusIoResource res = this.next;
      this.next = null;
      return res;
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
    public void close() throws IOException {
      if (this.it instanceof Closeable)
        ((Closeable)this.it).close(); 
    }
  }
  
  public Stream stream() {
    return new Stream() {
        public void forEach(PlexusIoResourceConsumer resourceConsumer) throws IOException {
          Iterator<PlexusIoResource> it = AbstractPlexusIoArchiveResourceCollection.this.getEntries();
          while (it.hasNext()) {
            PlexusIoResource res = it.next();
            if (AbstractPlexusIoArchiveResourceCollection.this.isSelected(res))
              resourceConsumer.accept(res); 
          } 
          if (it instanceof Closeable)
            ((Closeable)it).close(); 
        }
      };
  }
  
  public long getLastModified() throws IOException {
    File f = getFile();
    return (f == null) ? 0L : f.lastModified();
  }
}
