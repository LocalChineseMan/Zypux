package org.codehaus.plexus.components.io.resources;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.PlexusIoResourceConsumer;

public abstract class PlexusIoCompressedFileResourceCollection implements PlexusIoArchivedResourceCollection, Iterable<PlexusIoResource> {
  private File file;
  
  private String path;
  
  private InputStreamTransformer streamTransformers = AbstractPlexusIoResourceCollection.identityTransformer;
  
  public File getFile() {
    return this.file;
  }
  
  public void setFile(File file) {
    this.file = file;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  protected abstract PlexusIoResourceAttributes getAttributes(File paramFile) throws IOException;
  
  public void setStreamTransformer(InputStreamTransformer streamTransformers) {
    this.streamTransformers = streamTransformers;
  }
  
  public Stream stream() {
    return new Stream() {
        public void forEach(PlexusIoResourceConsumer resourceConsumer) throws IOException {
          Iterator<PlexusIoResource> it = PlexusIoCompressedFileResourceCollection.this.getResources();
          while (it.hasNext())
            resourceConsumer.accept(it.next()); 
          if (it instanceof Closeable)
            ((Closeable)it).close(); 
        }
      };
  }
  
  public Iterator<PlexusIoResource> getResources() throws IOException {
    final File f = getFile();
    String p = ((getPath() == null) ? getName(f) : getPath()).replace('\\', '/');
    if (f == null)
      throw new IOException("No archive file is set."); 
    if (!f.isFile())
      throw new IOException("The archive file " + f.getPath() + " does not exist or is no file."); 
    PlexusIoResourceAttributes attributes = getAttributes(f);
    ContentSupplier contentSupplier = new ContentSupplier() {
        @Nonnull
        public InputStream getContents() throws IOException {
          return PlexusIoCompressedFileResourceCollection.this.getInputStream(f);
        }
      };
    PlexusIoResource resource = ResourceFactory.createResource(f, p, contentSupplier, attributes);
    return Collections.<PlexusIoResource>singleton(resource).iterator();
  }
  
  protected String getName(File file) throws IOException {
    String name = file.getPath();
    String ext = getDefaultExtension();
    if (ext != null && ext.length() > 0 && name.endsWith(ext))
      return name.substring(0, name.length() - ext.length()); 
    return name;
  }
  
  protected abstract String getDefaultExtension();
  
  @Nonnull
  protected abstract InputStream getInputStream(File paramFile) throws IOException;
  
  public InputStream getInputStream(PlexusIoResource resource) throws IOException {
    InputStream contents = resource.getContents();
    return new ClosingInputStream(this.streamTransformers.transform(resource, contents), contents);
  }
  
  public PlexusIoResource resolve(PlexusIoResource resource) throws IOException {
    Deferred deferred = new Deferred(resource, this, (this.streamTransformers != AbstractPlexusIoResourceCollection.identityTransformer));
    return deferred.asResource();
  }
  
  public Iterator<PlexusIoResource> iterator() {
    try {
      return getResources();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public String getName(PlexusIoResource resource) {
    return resource.getName();
  }
  
  public long getLastModified() throws IOException {
    File f = getFile();
    return (f == null) ? 0L : f.lastModified();
  }
  
  public boolean isConcurrentAccessSupported() {
    return true;
  }
}
