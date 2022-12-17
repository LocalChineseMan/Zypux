package org.codehaus.plexus.components.io.resources.proxy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.NameSupplier;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResourceCollectionWithAttributes;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.Stream;

public class PlexusIoProxyResourceCollection extends AbstractPlexusIoResourceCollectionWithAttributes implements EncodingSupported {
  private PlexusIoResourceCollection src;
  
  public PlexusIoProxyResourceCollection(@Nonnull PlexusIoResourceCollection src) {
    this.src = src;
  }
  
  public PlexusIoResourceCollection getSrc() {
    return this.src;
  }
  
  public void setDefaultAttributes(int uid, String userName, int gid, String groupName, int fileMode, int dirMode) {
    setDefaultFileAttributes((PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(uid), userName, Integer.valueOf(gid), groupName, fileMode));
    setDefaultDirAttributes((PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(uid), userName, Integer.valueOf(gid), groupName, dirMode));
  }
  
  public void setOverrideAttributes(int uid, String userName, int gid, String groupName, int fileMode, int dirMode) {
    setOverrideFileAttributes((PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(uid), userName, Integer.valueOf(gid), groupName, fileMode));
    setOverrideDirAttributes((PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(uid), userName, Integer.valueOf(gid), groupName, dirMode));
  }
  
  public void setStreamTransformer(InputStreamTransformer streamTransformer) {
    if (this.src instanceof AbstractPlexusIoResourceCollection)
      ((AbstractPlexusIoResourceCollection)this.src).setStreamTransformer(streamTransformer); 
    super.setStreamTransformer(streamTransformer);
  }
  
  protected FileSelector getDefaultFileSelector() {
    IncludeExcludeFileSelector fileSelector = new IncludeExcludeFileSelector();
    fileSelector.setIncludes(getIncludes());
    fileSelector.setExcludes(getExcludes());
    fileSelector.setCaseSensitive(isCaseSensitive());
    fileSelector.setUseDefaultExcludes(isUsingDefaultExcludes());
    return (FileSelector)fileSelector;
  }
  
  private String getNonEmptyPrfix() {
    String prefix = getPrefix();
    if (prefix != null && prefix.length() == 0)
      return null; 
    return prefix;
  }
  
  class FwdIterator extends ForwardingIterator {
    Iterator<PlexusIoResource> iter;
    
    private final FileSelector fileSelector = PlexusIoProxyResourceCollection.this.getDefaultFileSelector();
    
    private final String prefix = PlexusIoProxyResourceCollection.this.getNonEmptyPrfix();
    
    FwdIterator(Iterator<PlexusIoResource> resources) {
      super(resources);
      this.iter = resources;
    }
    
    protected PlexusIoResource getNextResource() throws IOException {
      if (!this.iter.hasNext())
        return null; 
      PlexusIoResource plexusIoResource = this.iter.next();
      while (!this.fileSelector.isSelected((FileInfo)plexusIoResource) || !PlexusIoProxyResourceCollection.this.isSelected(plexusIoResource) || (plexusIoResource
        .isDirectory() && !PlexusIoProxyResourceCollection.this.isIncludingEmptyDirectories())) {
        if (!this.iter.hasNext())
          return null; 
        plexusIoResource = this.iter.next();
      } 
      PlexusIoResourceAttributes attrs = null;
      if (plexusIoResource instanceof ResourceAttributeSupplier)
        attrs = ((ResourceAttributeSupplier)plexusIoResource).getAttributes(); 
      if (attrs == null)
        attrs = SimpleResourceAttributes.lastResortDummyAttributesForBrokenOS(); 
      attrs = PlexusIoProxyResourceCollection.this.mergeAttributes(attrs, plexusIoResource.isDirectory());
      if (this.prefix != null) {
        final String name = plexusIoResource.getName();
        final PlexusIoResourceAttributes attrs2 = attrs;
        PlexusIoProxyResourceCollection.DualSupplier supplier = new PlexusIoProxyResourceCollection.DualSupplier() {
            public String getName() {
              return PlexusIoProxyResourceCollection.FwdIterator.this.prefix + name;
            }
            
            public PlexusIoResourceAttributes getAttributes() {
              return attrs2;
            }
          };
        plexusIoResource = ProxyFactory.createProxy(plexusIoResource, supplier);
      } 
      return plexusIoResource;
    }
  }
  
  public Stream stream() {
    return getSrc().stream();
  }
  
  public Iterator<PlexusIoResource> getResources() throws IOException {
    return new FwdIterator(getSrc().getResources());
  }
  
  static abstract class DualSupplier implements NameSupplier, ResourceAttributeSupplier {}
  
  public String getName(PlexusIoResource resource) {
    String name = resource.getName();
    FileMapper[] mappers = getFileMappers();
    if (mappers != null)
      for (FileMapper mapper : mappers)
        name = mapper.getMappedFileName(name);  
    return name;
  }
  
  public long getLastModified() throws IOException {
    return this.src.getLastModified();
  }
  
  public void setEncoding(Charset charset) {
    if (this.src instanceof EncodingSupported)
      ((EncodingSupported)this.src).setEncoding(charset); 
  }
  
  public boolean isConcurrentAccessSupported() {
    return this.src.isConcurrentAccessSupported();
  }
}
