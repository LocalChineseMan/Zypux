package org.codehaus.plexus.archiver.diags;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.slf4j.Logger;

public class DryRunArchiver extends DelgatingArchiver {
  private final Logger logger;
  
  public DryRunArchiver(Archiver target, Logger logger) {
    super(target);
    this.logger = logger;
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix, String[] includes, String[] excludes) {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  private void debug(String message) {
    if (this.logger != null && this.logger.isDebugEnabled())
      this.logger.debug(message); 
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addArchivedFileSet(File archiveFile, String[] includes, String[] excludes) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addDirectory(@Nonnull File directory, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addSymlink(String symlinkName, String symlinkDestination) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addSymlink(String symlinkName, int permissions, String symlinkDestination) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addDirectory(@Nonnull File directory, String prefix) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addDirectory(@Nonnull File directory, String[] includes, String[] excludes) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addDirectory(@Nonnull File directory) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName, int permissions) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void createArchive() throws ArchiverException, IOException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void setDotFileDirectory(File dotFileDirectory) {
    throw new UnsupportedOperationException("Undocumented feature of plexus-archiver; this is not yet supported.");
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addFileSet(@Nonnull FileSet fileSet) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addResource(PlexusIoResource resource, String destFileName, int permissions) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  public void addResources(PlexusIoResourceCollection resources) throws ArchiverException {
    debug("DRY RUN: Skipping delegated call to: " + getMethodName());
  }
  
  private String getMethodName() {
    NullPointerException npe = new NullPointerException();
    StackTraceElement[] trace = npe.getStackTrace();
    StackTraceElement methodElement = trace[1];
    return methodElement.getMethodName() + " (archiver line: " + methodElement.getLineNumber() + ")";
  }
}
