package org.codehaus.plexus.components.io.resources;

import javax.annotation.Nonnull;

public abstract class AbstractPlexusIoResource implements PlexusIoResource {
  private final String name;
  
  private final long lastModified;
  
  private final long size;
  
  private final boolean isFile;
  
  private final boolean isDirectory;
  
  private final boolean isExisting;
  
  protected AbstractPlexusIoResource(@Nonnull String name, long lastModified, long size, boolean isFile, boolean isDirectory, boolean isExisting) {
    this.name = name;
    this.lastModified = lastModified;
    this.size = size;
    this.isFile = isFile;
    this.isDirectory = isDirectory;
    this.isExisting = isExisting;
  }
  
  public long getLastModified() {
    return this.lastModified;
  }
  
  @Nonnull
  public String getName() {
    return this.name;
  }
  
  public long getSize() {
    return this.size;
  }
  
  public boolean isDirectory() {
    return this.isDirectory;
  }
  
  public boolean isExisting() {
    return this.isExisting;
  }
  
  public boolean isFile() {
    return this.isFile;
  }
  
  public boolean isSymbolicLink() {
    return false;
  }
}
