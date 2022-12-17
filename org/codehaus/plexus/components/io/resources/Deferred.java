package org.codehaus.plexus.components.io.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.NameSupplier;
import org.codehaus.plexus.components.io.functions.SizeSupplier;
import org.codehaus.plexus.components.io.resources.proxy.ProxyFactory;

class Deferred implements ContentSupplier, NameSupplier, SizeSupplier {
  final DeferredFileOutputStream dfos;
  
  final PlexusIoResource resource;
  
  final PlexusIoResourceCollection owner;
  
  public Deferred(PlexusIoResource resource, PlexusIoResourceCollection owner, boolean hasTransformer) throws IOException {
    this.resource = resource;
    this.owner = owner;
    this.dfos = hasTransformer ? new DeferredFileOutputStream(5000000, "p-archiver", null, null) : null;
    if (this.dfos != null) {
      InputStream inputStream = owner.getInputStream(resource);
      IOUtils.copy(inputStream, (OutputStream)this.dfos);
      IOUtils.closeQuietly(inputStream);
    } 
  }
  
  @Nonnull
  public InputStream getContents() throws IOException {
    if (this.dfos == null)
      return this.resource.getContents(); 
    if (this.dfos.isInMemory())
      return new ByteArrayInputStream(this.dfos.getData()); 
    return new FileInputStream(this.dfos.getFile()) {
        public void close() throws IOException {
          super.close();
          Deferred.this.dfos.getFile().delete();
        }
      };
  }
  
  public long getSize() {
    if (this.dfos == null)
      return this.resource.getSize(); 
    if (this.dfos.isInMemory())
      return this.dfos.getByteCount(); 
    return this.dfos.getFile().length();
  }
  
  public String getName() {
    return this.owner.getName(this.resource);
  }
  
  public PlexusIoResource asResource() {
    return ProxyFactory.createProxy(this.resource, this);
  }
}
