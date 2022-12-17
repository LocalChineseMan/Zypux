package org.codehaus.plexus.archiver.diags;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
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

public class DelgatingArchiver implements Archiver {
  private final Archiver target;
  
  public DelgatingArchiver(Archiver target) {
    this.target = target;
  }
  
  public void createArchive() throws ArchiverException, IOException {
    this.target.createArchive();
  }
  
  @Deprecated
  public void addDirectory(@Nonnull File directory) throws ArchiverException {
    this.target.addDirectory(directory);
  }
  
  @Deprecated
  public void addDirectory(@Nonnull File directory, String prefix) throws ArchiverException {
    this.target.addDirectory(directory, prefix);
  }
  
  @Deprecated
  public void addDirectory(@Nonnull File directory, String[] includes, String[] excludes) throws ArchiverException {
    this.target.addDirectory(directory, includes, excludes);
  }
  
  public void addDirectory(@Nonnull File directory, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    this.target.addDirectory(directory, prefix, includes, excludes);
  }
  
  public void addFileSet(@Nonnull FileSet fileSet) throws ArchiverException {
    this.target.addFileSet(fileSet);
  }
  
  public void addSymlink(String symlinkName, String symlinkDestination) throws ArchiverException {
    this.target.addSymlink(symlinkName, symlinkDestination);
  }
  
  public void addSymlink(String symlinkName, int permissions, String symlinkDestination) throws ArchiverException {
    this.target.addSymlink(symlinkName, permissions, symlinkDestination);
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName) throws ArchiverException {
    this.target.addFile(inputFile, destFileName);
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName, int permissions) throws ArchiverException {
    this.target.addFile(inputFile, destFileName, permissions);
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile) throws ArchiverException {
    this.target.addArchivedFileSet(archiveFile);
  }
  
  @Deprecated
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix) throws ArchiverException {
    this.target.addArchivedFileSet(archiveFile, prefix);
  }
  
  public void addArchivedFileSet(File archiveFile, String[] includes, String[] excludes) throws ArchiverException {
    this.target.addArchivedFileSet(archiveFile, includes, excludes);
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    this.target.addArchivedFileSet(archiveFile, prefix, includes, excludes);
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet) throws ArchiverException {
    this.target.addArchivedFileSet(fileSet);
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {
    this.target.addArchivedFileSet(fileSet, charset);
  }
  
  public void addResource(PlexusIoResource resource, String destFileName, int permissions) throws ArchiverException {
    this.target.addResource(resource, destFileName, permissions);
  }
  
  public void addResources(PlexusIoResourceCollection resources) throws ArchiverException {
    this.target.addResources(resources);
  }
  
  public File getDestFile() {
    return this.target.getDestFile();
  }
  
  public void setDestFile(File destFile) {
    this.target.setDestFile(destFile);
  }
  
  public void setFileMode(int mode) {
    this.target.setFileMode(mode);
  }
  
  public int getFileMode() {
    return this.target.getFileMode();
  }
  
  public int getOverrideFileMode() {
    return this.target.getOverrideFileMode();
  }
  
  public void setDefaultFileMode(int mode) {
    this.target.setDefaultFileMode(mode);
  }
  
  public int getDefaultFileMode() {
    return this.target.getDefaultFileMode();
  }
  
  public void setDirectoryMode(int mode) {
    this.target.setDirectoryMode(mode);
  }
  
  public int getDirectoryMode() {
    return this.target.getDirectoryMode();
  }
  
  public int getOverrideDirectoryMode() {
    return this.target.getOverrideDirectoryMode();
  }
  
  public void setDefaultDirectoryMode(int mode) {
    this.target.setDefaultDirectoryMode(mode);
  }
  
  public int getDefaultDirectoryMode() {
    return this.target.getDefaultDirectoryMode();
  }
  
  public boolean getIncludeEmptyDirs() {
    return this.target.getIncludeEmptyDirs();
  }
  
  public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
    this.target.setIncludeEmptyDirs(includeEmptyDirs);
  }
  
  public void setDotFileDirectory(File dotFileDirectory) {
    this.target.setDotFileDirectory(dotFileDirectory);
  }
  
  @Nonnull
  public ResourceIterator getResources() throws ArchiverException {
    return this.target.getResources();
  }
  
  public Map<String, ArchiveEntry> getFiles() {
    return this.target.getFiles();
  }
  
  public boolean isForced() {
    return this.target.isForced();
  }
  
  public void setForced(boolean forced) {
    this.target.setForced(forced);
  }
  
  public boolean isSupportingForced() {
    return this.target.isSupportingForced();
  }
  
  public String getDuplicateBehavior() {
    return this.target.getDuplicateBehavior();
  }
  
  public void setDuplicateBehavior(String duplicate) {
    this.target.setDuplicateBehavior(duplicate);
  }
  
  public void setUseJvmChmod(boolean useJvmChmod) {
    this.target.setUseJvmChmod(useJvmChmod);
  }
  
  public boolean isUseJvmChmod() {
    return this.target.isUseJvmChmod();
  }
  
  public boolean isIgnorePermissions() {
    return this.target.isIgnorePermissions();
  }
  
  public void setIgnorePermissions(boolean ignorePermissions) {
    this.target.setIgnorePermissions(ignorePermissions);
  }
  
  @Deprecated
  public void setLastModifiedDate(Date lastModifiedDate) {
    this.target.setLastModifiedDate(lastModifiedDate);
  }
  
  @Deprecated
  public Date getLastModifiedDate() {
    return this.target.getLastModifiedDate();
  }
  
  public void setLastModifiedTime(FileTime lastModifiedTime) {
    this.target.setLastModifiedTime(lastModifiedTime);
  }
  
  public FileTime getLastModifiedTime() {
    return this.target.getLastModifiedTime();
  }
  
  public void setFilenameComparator(Comparator<String> filenameComparator) {
    this.target.setFilenameComparator(filenameComparator);
  }
  
  public void setOverrideUid(int uid) {
    this.target.setOverrideUid(uid);
  }
  
  public void setOverrideUserName(String userName) {
    this.target.setOverrideUserName(userName);
  }
  
  public int getOverrideUid() {
    return this.target.getOverrideUid();
  }
  
  public String getOverrideUserName() {
    return this.target.getOverrideUserName();
  }
  
  public void setOverrideGid(int gid) {
    this.target.setOverrideGid(gid);
  }
  
  public void setOverrideGroupName(String groupName) {
    this.target.setOverrideGroupName(groupName);
  }
  
  public int getOverrideGid() {
    return this.target.getOverrideGid();
  }
  
  public String getOverrideGroupName() {
    return this.target.getOverrideGroupName();
  }
  
  @Deprecated
  public void configureReproducible(Date lastModifiedDate) {
    this.target.configureReproducible(lastModifiedDate);
  }
  
  public void configureReproducibleBuild(FileTime lastModifiedTime) {
    this.target.configureReproducibleBuild(lastModifiedTime);
  }
}
