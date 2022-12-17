package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.resources.PlexusIoVirtualSymlinkResource;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.ResourceFactory;

public class ArchiveEntry {
  public static final String ROLE = ArchiveEntry.class.getName();
  
  public static final int FILE = 1;
  
  public static final int DIRECTORY = 2;
  
  public static final int SYMLINK = 3;
  
  @Nonnull
  private final PlexusIoResource resource;
  
  private final String name;
  
  private final int type;
  
  private final int mode;
  
  private final int defaultDirMode;
  
  private PlexusIoResourceAttributes attributes;
  
  private final boolean addSynchronously;
  
  private ArchiveEntry(String name, @Nonnull PlexusIoResource resource, int type, int mode, PlexusIoResourceCollection collection, int defaultDirMode) {
    try {
      this.name = name;
      this.defaultDirMode = defaultDirMode;
      this.resource = (collection != null) ? collection.resolve(resource) : resource;
      this
        .attributes = (resource instanceof ResourceAttributeSupplier) ? ((ResourceAttributeSupplier)resource).getAttributes() : null;
      this.type = type;
      int permissions = mode;
      if (mode == -1 && this.attributes == null)
        permissions = resource.isFile() ? 33188 : (resource.isSymbolicLink() ? 41471 : 16877); 
      this
        
        .mode = (permissions == -1) ? permissions : (permissions & 0xFFF | ((type == 1) ? 32768 : ((type == 3) ? 40960 : 16384)));
      this.addSynchronously = (collection != null && !collection.isConcurrentAccessSupported());
    } catch (IOException e) {
      throw new ArchiverException("Error resolving resource " + resource.getName(), e);
    } 
  }
  
  public String getName() {
    return this.name;
  }
  
  @Deprecated
  public File getFile() {
    if (this.resource instanceof PlexusIoFileResource)
      return ((PlexusIoFileResource)this.resource).getFile(); 
    return null;
  }
  
  public InputStream getInputStream() throws IOException {
    return this.resource.getContents();
  }
  
  public int getType() {
    return this.type;
  }
  
  public int getMode() {
    if (this.mode != -1)
      return this.mode; 
    if (this.attributes != null && this.attributes.getOctalMode() > -1)
      return this.attributes.getOctalMode(); 
    return ((this.type == 1) ? 
      33188 : (
      (this.type == 3) ? 
      41471 : 
      16877)) & 0xFFF | (
      (this.type == 1) ? 
      32768 : (
      (this.type == 3) ? 
      40960 : 
      16384));
  }
  
  public boolean shouldAddSynchronously() {
    return this.addSynchronously;
  }
  
  public static ArchiveEntry createFileEntry(String target, PlexusIoResource resource, int permissions, PlexusIoResourceCollection collection, int defaultDirectoryPermissions) throws ArchiverException {
    if (resource.isDirectory())
      throw new ArchiverException("Not a file: " + resource.getName()); 
    int type = resource.isSymbolicLink() ? 3 : 1;
    return new ArchiveEntry(target, resource, type, permissions, collection, defaultDirectoryPermissions);
  }
  
  public static ArchiveEntry createFileEntry(String target, File file, int permissions, int defaultDirectoryPermissions) throws ArchiverException, IOException {
    int type;
    if (!file.isFile())
      throw new ArchiverException("Not a file: " + file); 
    PlexusIoResource res = ResourceFactory.createResource(file);
    if (res.isSymbolicLink()) {
      type = 3;
      permissions &= 0xFFFF7FFF;
    } else {
      type = 1;
    } 
    return new ArchiveEntry(target, res, type, permissions, null, defaultDirectoryPermissions);
  }
  
  public static ArchiveEntry createDirectoryEntry(String target, @Nonnull PlexusIoResource resource, int permissions, int defaultDirectoryPermissions) throws ArchiverException {
    int type;
    if (!resource.isDirectory())
      throw new ArchiverException("Not a directory: " + resource.getName()); 
    if (resource.isSymbolicLink()) {
      type = 3;
      permissions &= 0xFFFFBFFF;
    } else {
      type = 2;
    } 
    return new ArchiveEntry(target, resource, type, permissions, null, defaultDirectoryPermissions);
  }
  
  public static ArchiveEntry createDirectoryEntry(String target, File file, int permissions, int defaultDirMode1) throws ArchiverException, IOException {
    if (!file.isDirectory())
      throw new ArchiverException("Not a directory: " + file); 
    PlexusIoResource res = ResourceFactory.createResource(file);
    return new ArchiveEntry(target, res, 2, permissions, null, defaultDirMode1);
  }
  
  public static ArchiveEntry createSymlinkEntry(String symlinkName, int permissions, String symlinkDestination, int defaultDirectoryPermissions) {
    ArchiveEntry archiveEntry = new ArchiveEntry(symlinkName, (PlexusIoResource)new PlexusIoVirtualSymlinkResource(new File(symlinkName), symlinkDestination), 3, permissions, null, defaultDirectoryPermissions);
    return archiveEntry;
  }
  
  public PlexusIoResourceAttributes getResourceAttributes() {
    return this.attributes;
  }
  
  public void setResourceAttributes(PlexusIoResourceAttributes attributes) {
    this.attributes = attributes;
  }
  
  @Nonnull
  public PlexusIoResource getResource() {
    return this.resource;
  }
  
  public int getDefaultDirMode() {
    return this.defaultDirMode;
  }
}
