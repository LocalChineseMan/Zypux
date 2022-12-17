package org.codehaus.plexus.archiver.util;

import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.BaseFileSet;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

public abstract class AbstractFileSet<T extends AbstractFileSet> implements BaseFileSet {
  private String prefix;
  
  private String[] includes;
  
  private String[] excludes;
  
  private FileSelector[] fileSelectors;
  
  private boolean caseSensitive = true;
  
  private boolean usingDefaultExcludes = true;
  
  private boolean includingEmptyDirectories = true;
  
  private InputStreamTransformer streamTransformer = null;
  
  private FileMapper[] fileMappers;
  
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
  
  public T prefixed(String prefix) {
    setPrefix(prefix);
    return (T)this;
  }
  
  public T include(String[] includes) {
    setIncludes(includes);
    return (T)this;
  }
  
  public T exclude(String[] excludes) {
    setExcludes(excludes);
    return (T)this;
  }
  
  public T includeExclude(String[] includes, String[] excludes) {
    return include(includes).exclude(excludes);
  }
  
  public T includeEmptyDirs(boolean includeEmptyDirectories) {
    setIncludingEmptyDirectories(includeEmptyDirectories);
    return (T)this;
  }
  
  public void setStreamTransformer(@Nonnull InputStreamTransformer streamTransformer) {
    this.streamTransformer = streamTransformer;
  }
  
  public InputStreamTransformer getStreamTransformer() {
    return this.streamTransformer;
  }
  
  public void setFileMappers(FileMapper[] fileMappers) {
    this.fileMappers = fileMappers;
  }
  
  public FileMapper[] getFileMappers() {
    return this.fileMappers;
  }
}
