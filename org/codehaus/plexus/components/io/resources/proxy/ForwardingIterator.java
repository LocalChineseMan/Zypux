package org.codehaus.plexus.components.io.resources.proxy;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

abstract class ForwardingIterator implements Iterator<PlexusIoResource>, Closeable {
  private final Object possiblyCloseable;
  
  private PlexusIoResource next = null;
  
  ForwardingIterator(Object possiblyCloseable) {
    this.possiblyCloseable = possiblyCloseable;
  }
  
  public boolean hasNext() {
    if (this.next == null)
      try {
        this.next = getNextResource();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }  
    return (this.next != null);
  }
  
  public PlexusIoResource next() {
    if (!hasNext())
      throw new NoSuchElementException(); 
    PlexusIoResource ret = this.next;
    this.next = null;
    return ret;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  public void close() throws IOException {
    if (this.possiblyCloseable instanceof Closeable)
      ((Closeable)this.possiblyCloseable).close(); 
  }
  
  protected abstract PlexusIoResource getNextResource() throws IOException;
}
