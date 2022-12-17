package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

public interface Archiver {
  public static final int DEFAULT_DIR_MODE = 16877;
  
  public static final int DEFAULT_FILE_MODE = 33188;
  
  public static final int DEFAULT_SYMLILNK_MODE = 41471;
  
  public static final String DUPLICATES_ADD = "add";
  
  public static final String DUPLICATES_PRESERVE = "preserve";
  
  public static final String DUPLICATES_SKIP = "skip";
  
  public static final String DUPLICATES_FAIL = "fail";
  
  public static final Set<String> DUPLICATES_VALID_BEHAVIORS = new HashSet<String>() {
      private static final long serialVersionUID = 1L;
    };
  
  void createArchive() throws ArchiverException, IOException;
  
  @Deprecated
  void addDirectory(@Nonnull File paramFile) throws ArchiverException;
  
  @Deprecated
  void addDirectory(@Nonnull File paramFile, String paramString) throws ArchiverException;
  
  @Deprecated
  void addDirectory(@Nonnull File paramFile, String[] paramArrayOfString1, String[] paramArrayOfString2) throws ArchiverException;
  
  @Deprecated
  void addDirectory(@Nonnull File paramFile, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2) throws ArchiverException;
  
  void addFileSet(@Nonnull FileSet paramFileSet) throws ArchiverException;
  
  void addSymlink(String paramString1, String paramString2) throws ArchiverException;
  
  void addSymlink(String paramString1, int paramInt, String paramString2) throws ArchiverException;
  
  void addFile(@Nonnull File paramFile, @Nonnull String paramString) throws ArchiverException;
  
  void addFile(@Nonnull File paramFile, @Nonnull String paramString, int paramInt) throws ArchiverException;
  
  void addArchivedFileSet(@Nonnull File paramFile) throws ArchiverException;
  
  @Deprecated
  void addArchivedFileSet(@Nonnull File paramFile, String paramString) throws ArchiverException;
  
  @Deprecated
  void addArchivedFileSet(File paramFile, String[] paramArrayOfString1, String[] paramArrayOfString2) throws ArchiverException;
  
  @Deprecated
  void addArchivedFileSet(@Nonnull File paramFile, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2) throws ArchiverException;
  
  void addArchivedFileSet(ArchivedFileSet paramArchivedFileSet) throws ArchiverException;
  
  void addArchivedFileSet(ArchivedFileSet paramArchivedFileSet, Charset paramCharset) throws ArchiverException;
  
  void addResource(PlexusIoResource paramPlexusIoResource, String paramString, int paramInt) throws ArchiverException;
  
  void addResources(PlexusIoResourceCollection paramPlexusIoResourceCollection) throws ArchiverException;
  
  File getDestFile();
  
  void setDestFile(File paramFile);
  
  void setFileMode(int paramInt);
  
  int getFileMode();
  
  int getOverrideFileMode();
  
  void setDefaultFileMode(int paramInt);
  
  int getDefaultFileMode();
  
  void setDirectoryMode(int paramInt);
  
  int getDirectoryMode();
  
  int getOverrideDirectoryMode();
  
  void setDefaultDirectoryMode(int paramInt);
  
  int getDefaultDirectoryMode();
  
  boolean getIncludeEmptyDirs();
  
  void setIncludeEmptyDirs(boolean paramBoolean);
  
  void setDotFileDirectory(File paramFile);
  
  @Nonnull
  ResourceIterator getResources() throws ArchiverException;
  
  Map<String, ArchiveEntry> getFiles();
  
  boolean isForced();
  
  void setForced(boolean paramBoolean);
  
  boolean isSupportingForced();
  
  String getDuplicateBehavior();
  
  void setDuplicateBehavior(String paramString);
  
  @Deprecated
  void setUseJvmChmod(boolean paramBoolean);
  
  @Deprecated
  boolean isUseJvmChmod();
  
  boolean isIgnorePermissions();
  
  void setIgnorePermissions(boolean paramBoolean);
  
  @Deprecated
  void setLastModifiedDate(Date paramDate);
  
  @Deprecated
  Date getLastModifiedDate();
  
  void setLastModifiedTime(FileTime paramFileTime);
  
  FileTime getLastModifiedTime();
  
  void setFilenameComparator(Comparator<String> paramComparator);
  
  void setOverrideUid(int paramInt);
  
  void setOverrideUserName(String paramString);
  
  int getOverrideUid();
  
  String getOverrideUserName();
  
  void setOverrideGid(int paramInt);
  
  void setOverrideGroupName(String paramString);
  
  int getOverrideGid();
  
  String getOverrideGroupName();
  
  @Deprecated
  void configureReproducible(Date paramDate);
  
  void configureReproducibleBuild(FileTime paramFileTime);
}
