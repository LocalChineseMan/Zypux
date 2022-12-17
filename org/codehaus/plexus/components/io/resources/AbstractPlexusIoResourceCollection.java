package org.codehaus.plexus.components.io.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.filemappers.PrefixFileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

public abstract class AbstractPlexusIoResourceCollection implements PlexusIoResourceCollection {
  static class IdentityTransformer implements InputStreamTransformer {
    @Nonnull
    public InputStream transform(@Nonnull PlexusIoResource resource, @Nonnull InputStream inputStream) throws IOException {
      return inputStream;
    }
  }
  
  public static final InputStreamTransformer identityTransformer = new IdentityTransformer();
  
  private String prefix;
  
  private String[] includes;
  
  private String[] excludes;
  
  private FileSelector[] fileSelectors;
  
  private boolean caseSensitive = true;
  
  private boolean usingDefaultExcludes = true;
  
  private boolean includingEmptyDirectories = true;
  
  private FileMapper[] fileMappers;
  
  private InputStreamTransformer streamTransformer = identityTransformer;
  
  public void setExcludes(String[] excludes) {
    this.excludes = excludes;
  }
  
  public String[] getExcludes() {
    return this.excludes;
  }
  
  public void setFileSelectors(FileSelector[] fileSelectors) {
    this.fileSelectors = fileSelectors;
  }
  
  public FileSelector[] getFileSelectors() {
    return this.fileSelectors;
  }
  
  public void setStreamTransformer(InputStreamTransformer streamTransformer) {
    if (streamTransformer == null) {
      this.streamTransformer = identityTransformer;
    } else {
      this.streamTransformer = streamTransformer;
    } 
  }
  
  protected InputStreamTransformer getStreamTransformer() {
    return this.streamTransformer;
  }
  
  public void setIncludes(String[] includes) {
    this.includes = includes;
  }
  
  public String[] getIncludes() {
    return this.includes;
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }
  
  public boolean isCaseSensitive() {
    return this.caseSensitive;
  }
  
  public void setUsingDefaultExcludes(boolean usingDefaultExcludes) {
    this.usingDefaultExcludes = usingDefaultExcludes;
  }
  
  public boolean isUsingDefaultExcludes() {
    return this.usingDefaultExcludes;
  }
  
  public void setIncludingEmptyDirectories(boolean includingEmptyDirectories) {
    this.includingEmptyDirectories = includingEmptyDirectories;
  }
  
  public boolean isIncludingEmptyDirectories() {
    return this.includingEmptyDirectories;
  }
  
  protected boolean isSelected(PlexusIoResource plexusIoResource) throws IOException {
    FileSelector[] fileSelectors = getFileSelectors();
    if (fileSelectors != null)
      for (FileSelector fileSelector : fileSelectors) {
        if (!fileSelector.isSelected(plexusIoResource))
          return false; 
      }  
    return true;
  }
  
  public FileMapper[] getFileMappers() {
    return this.fileMappers;
  }
  
  public void setFileMappers(FileMapper[] fileMappers) {
    this.fileMappers = fileMappers;
  }
  
  public Iterator<PlexusIoResource> iterator() {
    try {
      return getResources();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public String getName(PlexusIoResource resource) {
    return getName(resource.getName());
  }
  
  protected String getName(String resourceName) {
    String name = resourceName;
    FileMapper[] mappers = getFileMappers();
    if (mappers != null)
      for (FileMapper mapper : mappers)
        name = mapper.getMappedFileName(name);  
    return PrefixFileMapper.getMappedFileName(getPrefix(), name);
  }
  
  public InputStream getInputStream(PlexusIoResource resource) throws IOException {
    InputStream contents = resource.getContents();
    return new ClosingInputStream(this.streamTransformer.transform(resource, contents), contents);
  }
  
  public PlexusIoResource resolve(PlexusIoResource resource) throws IOException {
    Deferred deferred = new Deferred(resource, this, (this.streamTransformer != identityTransformer));
    return deferred.asResource();
  }
  
  public long getLastModified() throws IOException {
    long lastModified = 0L;
    for (Iterator<PlexusIoResource> iter = getResources(); iter.hasNext(); ) {
      PlexusIoResource res = iter.next();
      long l = res.getLastModified();
      if (l == 0L)
        return 0L; 
      if (lastModified == 0L || l > lastModified)
        lastModified = l; 
    } 
    return lastModified;
  }
}
