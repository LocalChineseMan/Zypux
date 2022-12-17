package org.codehaus.plexus.archiver.diags;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

public class NoOpArchiver implements Archiver {
  boolean useJvmChmod;
  
  private boolean ignorePermissions;
  
  public void createArchive() throws ArchiverException, IOException {}
  
  public void addDirectory(@Nonnull File directory) throws ArchiverException {}
  
  public void addDirectory(@Nonnull File directory, String prefix) throws ArchiverException {}
  
  public void addDirectory(@Nonnull File directory, String[] includes, String[] excludes) throws ArchiverException {}
  
  public void addDirectory(@Nonnull File directory, String prefix, String[] includes, String[] excludes) throws ArchiverException {}
  
  public void addFileSet(@Nonnull FileSet fileSet) throws ArchiverException {}
  
  public void addSymlink(String symlinkName, String symlinkDestination) throws ArchiverException {}
  
  public void addSymlink(String symlinkName, int permissions, String symlinkDestination) throws ArchiverException {}
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName) throws ArchiverException {}
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName, int permissions) throws ArchiverException {}
  
  public void addArchivedFileSet(@Nonnull File archiveFile) throws ArchiverException {}
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix) throws ArchiverException {}
  
  public void addArchivedFileSet(File archiveFile, String[] includes, String[] excludes) throws ArchiverException {}
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix, String[] includes, String[] excludes) throws ArchiverException {}
  
  public void addArchivedFileSet(ArchivedFileSet fileSet) throws ArchiverException {}
  
  public void addArchivedFileSet(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {}
  
  public void addResource(PlexusIoResource resource, String destFileName, int permissions) throws ArchiverException {}
  
  public void addResources(PlexusIoResourceCollection resources) throws ArchiverException {}
  
  public File getDestFile() {
    return null;
  }
  
  public void setDestFile(File destFile) {}
  
  public void setFileMode(int mode) {}
  
  public int getFileMode() {
    return 0;
  }
  
  public int getOverrideFileMode() {
    return 0;
  }
  
  public void setDefaultFileMode(int mode) {}
  
  public int getDefaultFileMode() {
    return 0;
  }
  
  public void setDirectoryMode(int mode) {}
  
  public int getDirectoryMode() {
    return 0;
  }
  
  public int getOverrideDirectoryMode() {
    return 0;
  }
  
  public void setDefaultDirectoryMode(int mode) {}
  
  public int getDefaultDirectoryMode() {
    return 0;
  }
  
  public boolean getIncludeEmptyDirs() {
    return false;
  }
  
  public void setIncludeEmptyDirs(boolean includeEmptyDirs) {}
  
  public void setDotFileDirectory(File dotFileDirectory) {}
  
  @Nonnull
  public ResourceIterator getResources() throws ArchiverException {
    return new ResourceIterator() {
        public boolean hasNext() {
          return false;
        }
        
        public ArchiveEntry next() {
          return null;
        }
        
        public void remove() {
          throw new UnsupportedOperationException("remove");
        }
      };
  }
  
  public Map<String, ArchiveEntry> getFiles() {
    return Collections.emptyMap();
  }
  
  public boolean isForced() {
    return false;
  }
  
  public void setForced(boolean forced) {}
  
  public boolean isSupportingForced() {
    return false;
  }
  
  public String getDuplicateBehavior() {
    return null;
  }
  
  public void setDuplicateBehavior(String duplicate) {}
  
  public void setUseJvmChmod(boolean useJvmChmod) {
    this.useJvmChmod = useJvmChmod;
  }
  
  public boolean isUseJvmChmod() {
    return this.useJvmChmod;
  }
  
  public boolean isIgnorePermissions() {
    return this.ignorePermissions;
  }
  
  public void setIgnorePermissions(boolean ignorePermissions) {
    this.ignorePermissions = ignorePermissions;
  }
  
  @Deprecated
  public void setLastModifiedDate(Date lastModifiedDate) {}
  
  @Deprecated
  public Date getLastModifiedDate() {
    return null;
  }
  
  public void setLastModifiedTime(FileTime lastModifiedTime) {}
  
  public FileTime getLastModifiedTime() {
    return null;
  }
  
  public void setFilenameComparator(Comparator<String> filenameComparator) {}
  
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
