package org.codehaus.plexus.archiver.diags;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.util.StringUtils;

public class TrackingArchiver implements Archiver {
  private File destFile;
  
  public final List<Addition> added = new ArrayList<>();
  
  private boolean useJvmChmod;
  
  private boolean ignorePermissions;
  
  private FileTime lastModifiedTime;
  
  private Comparator<String> filenameComparator;
  
  public void createArchive() throws ArchiverException, IOException {}
  
  public void addDirectory(@Nonnull File directory) throws ArchiverException {
    this.added.add(new Addition(directory, null, null, null, -1));
  }
  
  public void addDirectory(@Nonnull File directory, String prefix) throws ArchiverException {
    this.added.add(new Addition(directory, prefix, null, null, -1));
  }
  
  public void addDirectory(@Nonnull File directory, String[] includes, String[] excludes) throws ArchiverException {
    this.added.add(new Addition(directory, null, includes, excludes, -1));
  }
  
  public void addDirectory(@Nonnull File directory, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    this.added.add(new Addition(directory, prefix, includes, excludes, -1));
  }
  
  public void addFileSet(@Nonnull FileSet fileSet) throws ArchiverException {
    this.added.add(new Addition(fileSet, null, null, null, -1));
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName) throws ArchiverException {
    this.added.add(new Addition(inputFile, destFileName, null, null, -1));
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName, int permissions) throws ArchiverException {
    this.added.add(new Addition(inputFile, destFileName, null, null, permissions));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile) throws ArchiverException {
    this.added.add(new Addition(archiveFile, null, null, null, -1));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix) throws ArchiverException {
    this.added.add(new Addition(archiveFile, prefix, null, null, -1));
  }
  
  public void addSymlink(String s, String s2) throws ArchiverException {
    this.added.add(new Addition(s, null, null, null, -1));
  }
  
  public void addSymlink(String s, int i, String s2) throws ArchiverException {
    this.added.add(new Addition(s, null, null, null, -1));
  }
  
  public void addArchivedFileSet(File archiveFile, String[] includes, String[] excludes) throws ArchiverException {
    this.added.add(new Addition(archiveFile, null, includes, excludes, -1));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    this.added.add(new Addition(archiveFile, prefix, includes, excludes, -1));
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet) throws ArchiverException {
    this.added.add(new Addition(fileSet, null, null, null, -1));
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {
    this.added.add(new Addition(fileSet, null, null, null, -1));
  }
  
  public void addResource(PlexusIoResource resource, String destFileName, int permissions) throws ArchiverException {
    this.added.add(new Addition(resource, destFileName, null, null, permissions));
  }
  
  public void addResources(PlexusIoResourceCollection resources) throws ArchiverException {
    this.added.add(new Addition(resources, null, null, null, -1));
  }
  
  public File getDestFile() {
    return this.destFile;
  }
  
  public void setDestFile(File destFile) {
    this.destFile = destFile;
  }
  
  public void setFileMode(int mode) {}
  
  public int getFileMode() {
    return Integer.parseInt("0644", 8);
  }
  
  public int getOverrideFileMode() {
    return Integer.parseInt("0644", 8);
  }
  
  public void setDefaultFileMode(int mode) {}
  
  public int getDefaultFileMode() {
    return Integer.parseInt("0644", 8);
  }
  
  public void setDirectoryMode(int mode) {}
  
  public int getDirectoryMode() {
    return Integer.parseInt("0755", 8);
  }
  
  public int getOverrideDirectoryMode() {
    return Integer.parseInt("0755", 8);
  }
  
  public void setDefaultDirectoryMode(int mode) {}
  
  public int getDefaultDirectoryMode() {
    return Integer.parseInt("0755", 8);
  }
  
  public boolean getIncludeEmptyDirs() {
    return false;
  }
  
  public void setIncludeEmptyDirs(boolean includeEmptyDirs) {}
  
  public void setDotFileDirectory(File dotFileDirectory) {}
  
  @Nonnull
  public ResourceIterator getResources() throws ArchiverException {
    throw new RuntimeException("Not implemented");
  }
  
  public Map<String, ArchiveEntry> getFiles() {
    return new HashMap<>();
  }
  
  public boolean isForced() {
    return false;
  }
  
  public void setForced(boolean forced) {}
  
  public boolean isSupportingForced() {
    return true;
  }
  
  public String getDuplicateBehavior() {
    return null;
  }
  
  public void setDuplicateBehavior(String duplicate) {}
  
  public class Addition {
    public final Object resource;
    
    public final File directory;
    
    public final String destination;
    
    public final int permissions;
    
    public final String[] includes;
    
    public final String[] excludes;
    
    public String toString() {
      return "Addition (\n    resource= " + this.resource + "\n    directory= " + this.directory + "\n    destination= " + this.destination + "\n    permissions= " + this.permissions + "\n    includes= " + (
        
        (this.includes == null) ? "-none-" : StringUtils.join((Object[])this.includes, ", ")) + "\n    excludes= " + (
        
        (this.excludes == null) ? "-none-" : StringUtils.join((Object[])this.excludes, ", ")) + "\n)";
    }
    
    public Addition(Object resource, String destination, String[] includes, String[] excludes, int permissions) {
      this.resource = resource;
      if (resource instanceof FileSet) {
        FileSet fs = (FileSet)resource;
        this.directory = fs.getDirectory();
        this.destination = fs.getPrefix();
        this.includes = fs.getIncludes();
        this.excludes = fs.getExcludes();
        this.permissions = permissions;
      } else {
        if (resource instanceof File && ((File)resource).isDirectory()) {
          this.directory = (File)resource;
        } else {
          this.directory = null;
        } 
        this.destination = destination;
        this.includes = includes;
        this.excludes = excludes;
        this.permissions = permissions;
      } 
    }
  }
  
  public boolean isUseJvmChmod() {
    return this.useJvmChmod;
  }
  
  public void setUseJvmChmod(boolean useJvmChmod) {
    this.useJvmChmod = useJvmChmod;
  }
  
  public boolean isIgnorePermissions() {
    return this.ignorePermissions;
  }
  
  public void setIgnorePermissions(boolean ignorePermissions) {
    this.ignorePermissions = ignorePermissions;
  }
  
  @Deprecated
  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedTime = (lastModifiedDate != null) ? FileTime.fromMillis(lastModifiedDate.getTime()) : null;
  }
  
  @Deprecated
  public Date getLastModifiedDate() {
    return (this.lastModifiedTime != null) ? new Date(this.lastModifiedTime.toMillis()) : null;
  }
  
  public void setLastModifiedTime(FileTime lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }
  
  public FileTime getLastModifiedTime() {
    return this.lastModifiedTime;
  }
  
  public void setFilenameComparator(Comparator<String> filenameComparator) {
    this.filenameComparator = filenameComparator;
  }
  
  public Comparator<String> getFilenameComparator() {
    return this.filenameComparator;
  }
  
  public void setOverrideUid(int uid) {}
  
  public void setOverrideUserName(String userName) {}
  
  public int getOverrideUid() {
    return 0;
  }
  
  public String getOverrideUserName() {
    return null;
  }
  
  public void setOverrideGid(int gid) {}
  
  public void setOverrideGroupName(String groupName) {}
  
  public int getOverrideGid() {
    return 0;
  }
  
  public String getOverrideGroupName() {
    return null;
  }
  
  @Deprecated
  public void configureReproducible(Date lastModifiedDate) {}
  
  public void configureReproducibleBuild(FileTime lastModifiedTime) {}
}
