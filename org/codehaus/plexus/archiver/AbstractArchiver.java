package org.codehaus.plexus.archiver;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultArchivedFileSet;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.attributes.SimpleResourceAttributes;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.AbstractPlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.EncodingSupported;
import org.codehaus.plexus.components.io.resources.PlexusIoArchivedResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.components.io.resources.proxy.PlexusIoProxyResourceCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractArchiver implements Archiver, FinalizerEnabled {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  private File destFile;
  
  protected Logger getLogger() {
    return this.logger;
  }
  
  private final List<Object> resources = new ArrayList();
  
  private boolean includeEmptyDirs = true;
  
  private int forcedFileMode = -1;
  
  private int forcedDirectoryMode = -1;
  
  private int defaultFileMode = -1;
  
  private int defaultDirectoryMode = -1;
  
  private boolean forced = true;
  
  private List<ArchiveFinalizer> finalizers;
  
  private File dotFileDirectory;
  
  private String duplicateBehavior = "skip";
  
  private final boolean replacePathSlashesToJavaPaths = (File.separatorChar == '/');
  
  private final List<Closeable> closeables = new ArrayList<>();
  
  private boolean useJvmChmod = true;
  
  private FileTime lastModifiedTime;
  
  private Comparator<String> filenameComparator;
  
  private int overrideUid = -1;
  
  private String overrideUserName;
  
  private int overrideGid = -1;
  
  private String overrideGroupName;
  
  @Inject
  private Provider<ArchiverManager> archiverManagerProvider;
  
  private static class AddedResourceCollection {
    private final PlexusIoResourceCollection resources;
    
    private final int forcedFileMode;
    
    private final int forcedDirectoryMode;
    
    public AddedResourceCollection(PlexusIoResourceCollection resources, int forcedFileMode, int forcedDirMode) {
      this.resources = resources;
      this.forcedFileMode = forcedFileMode;
      this.forcedDirectoryMode = forcedDirMode;
    }
    
    private int maybeOverridden(int suggestedMode, boolean isDir) {
      if (isDir)
        return (this.forcedDirectoryMode >= 0) ? this.forcedDirectoryMode : suggestedMode; 
      return (this.forcedFileMode >= 0) ? this.forcedFileMode : suggestedMode;
    }
  }
  
  private boolean ignorePermissions = false;
  
  public String getDuplicateBehavior() {
    return this.duplicateBehavior;
  }
  
  public void setDuplicateBehavior(String duplicate) {
    if (!Archiver.DUPLICATES_VALID_BEHAVIORS.contains(duplicate))
      throw new IllegalArgumentException("Invalid duplicate-file behavior: '" + duplicate + "'. Please specify one of: " + Archiver.DUPLICATES_VALID_BEHAVIORS); 
    this.duplicateBehavior = duplicate;
  }
  
  public final void setFileMode(int mode) {
    if (mode >= 0) {
      this.forcedFileMode = mode & 0xFFF | 0x8000;
    } else {
      this.forcedFileMode = -1;
    } 
  }
  
  public final void setDefaultFileMode(int mode) {
    this.defaultFileMode = mode & 0xFFF | 0x8000;
  }
  
  public final int getOverrideFileMode() {
    return this.forcedFileMode;
  }
  
  public final int getFileMode() {
    if (this.forcedFileMode < 0) {
      if (this.defaultFileMode < 0)
        return 33188; 
      return this.defaultFileMode;
    } 
    return this.forcedFileMode;
  }
  
  public final int getDefaultFileMode() {
    return this.defaultFileMode;
  }
  
  @Deprecated
  public final int getRawDefaultFileMode() {
    return getDefaultFileMode();
  }
  
  public final void setDirectoryMode(int mode) {
    if (mode >= 0) {
      this.forcedDirectoryMode = mode & 0xFFF | 0x4000;
    } else {
      this.forcedDirectoryMode = -1;
    } 
  }
  
  public final void setDefaultDirectoryMode(int mode) {
    this.defaultDirectoryMode = mode & 0xFFF | 0x4000;
  }
  
  public final int getOverrideDirectoryMode() {
    return this.forcedDirectoryMode;
  }
  
  public final int getDirectoryMode() {
    if (this.forcedDirectoryMode < 0) {
      if (this.defaultDirectoryMode < 0)
        return 16877; 
      return this.defaultDirectoryMode;
    } 
    return this.forcedDirectoryMode;
  }
  
  public final int getDefaultDirectoryMode() {
    if (this.defaultDirectoryMode < 0)
      return 16877; 
    return this.defaultDirectoryMode;
  }
  
  public boolean getIncludeEmptyDirs() {
    return this.includeEmptyDirs;
  }
  
  public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
    this.includeEmptyDirs = includeEmptyDirs;
  }
  
  public void addDirectory(@Nonnull File directory) throws ArchiverException {
    addFileSet(
        (FileSet)((DefaultFileSet)((DefaultFileSet)DefaultFileSet.fileSet(directory).prefixed("")).includeExclude(null, null)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addDirectory(@Nonnull File directory, String prefix) throws ArchiverException {
    addFileSet(
        (FileSet)((DefaultFileSet)((DefaultFileSet)DefaultFileSet.fileSet(directory).prefixed(prefix)).includeExclude(null, null)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addDirectory(@Nonnull File directory, String[] includes, String[] excludes) throws ArchiverException {
    addFileSet((FileSet)((DefaultFileSet)((DefaultFileSet)DefaultFileSet.fileSet(directory).prefixed("")).includeExclude(includes, excludes)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addDirectory(@Nonnull File directory, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    addFileSet((FileSet)((DefaultFileSet)((DefaultFileSet)DefaultFileSet.fileSet(directory).prefixed(prefix)).includeExclude(includes, excludes)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addFileSet(@Nonnull FileSet fileSet) throws ArchiverException {
    File directory = fileSet.getDirectory();
    if (directory == null)
      throw new ArchiverException("The file sets base directory is null."); 
    if (!directory.isDirectory())
      throw new ArchiverException(directory.getAbsolutePath() + " isn't a directory."); 
    PlexusIoFileResourceCollection collection = new PlexusIoFileResourceCollection();
    collection.setFollowingSymLinks(false);
    collection.setIncludes(fileSet.getIncludes());
    collection.setExcludes(fileSet.getExcludes());
    collection.setBaseDir(directory);
    collection.setFileSelectors(fileSet.getFileSelectors());
    collection.setIncludingEmptyDirectories(fileSet.isIncludingEmptyDirectories());
    collection.setPrefix(fileSet.getPrefix());
    collection.setCaseSensitive(fileSet.isCaseSensitive());
    collection.setUsingDefaultExcludes(fileSet.isUsingDefaultExcludes());
    collection.setStreamTransformer(fileSet.getStreamTransformer());
    collection.setFileMappers(fileSet.getFileMappers());
    collection.setFilenameComparator(getFilenameComparator());
    if (getOverrideDirectoryMode() > -1 || getOverrideFileMode() > -1 || getOverrideUid() > -1 || 
      getOverrideGid() > -1 || getOverrideUserName() != null || getOverrideGroupName() != null)
      collection.setOverrideAttributes(getOverrideUid(), getOverrideUserName(), getOverrideGid(), 
          getOverrideGroupName(), getOverrideFileMode(), 
          getOverrideDirectoryMode()); 
    if (getDefaultDirectoryMode() > -1 || getDefaultFileMode() > -1)
      collection.setDefaultAttributes(-1, null, -1, null, getDefaultFileMode(), getDefaultDirectoryMode()); 
    addResources((PlexusIoResourceCollection)collection);
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName) throws ArchiverException {
    int fileMode = getOverrideFileMode();
    addFile(inputFile, destFileName, fileMode);
  }
  
  public void addSymlink(String symlinkName, String symlinkDestination) throws ArchiverException {
    int fileMode = getOverrideFileMode();
    addSymlink(symlinkName, fileMode, symlinkDestination);
  }
  
  public void addSymlink(String symlinkName, int permissions, String symlinkDestination) throws ArchiverException {
    doAddResource(
        ArchiveEntry.createSymlinkEntry(symlinkName, permissions, symlinkDestination, getDirectoryMode()));
  }
  
  private ArchiveEntry updateArchiveEntryAttributes(ArchiveEntry entry) {
    if (getOverrideUid() > -1 || getOverrideGid() > -1 || getOverrideUserName() != null || 
      getOverrideGroupName() != null)
      entry.setResourceAttributes((PlexusIoResourceAttributes)new SimpleResourceAttributes(Integer.valueOf(getOverrideUid()), getOverrideUserName(), 
            Integer.valueOf(getOverrideGid()), getOverrideGroupName(), entry
            .getMode())); 
    return entry;
  }
  
  protected ArchiveEntry asArchiveEntry(@Nonnull PlexusIoResource resource, String destFileName, int permissions, PlexusIoResourceCollection collection) throws ArchiverException {
    ArchiveEntry entry;
    if (!resource.isExisting())
      throw new ArchiverException(resource.getName() + " not found."); 
    if (resource.isFile()) {
      entry = ArchiveEntry.createFileEntry(destFileName, resource, permissions, collection, getDirectoryMode());
    } else {
      entry = ArchiveEntry.createDirectoryEntry(destFileName, resource, permissions, getDirectoryMode());
    } 
    return updateArchiveEntryAttributes(entry);
  }
  
  private ArchiveEntry asArchiveEntry(AddedResourceCollection collection, PlexusIoResource resource) throws ArchiverException {
    String destFileName = collection.resources.getName(resource);
    int fromResource = -1;
    if (resource instanceof ResourceAttributeSupplier) {
      PlexusIoResourceAttributes attrs = ((ResourceAttributeSupplier)resource).getAttributes();
      if (attrs != null)
        fromResource = attrs.getOctalMode(); 
    } 
    return asArchiveEntry(resource, destFileName, collection
        .maybeOverridden(fromResource, resource.isDirectory()), collection
        .resources);
  }
  
  public void addResource(PlexusIoResource resource, String destFileName, int permissions) throws ArchiverException {
    doAddResource(asArchiveEntry(resource, destFileName, permissions, null));
  }
  
  public void addFile(@Nonnull File inputFile, @Nonnull String destFileName, int permissions) throws ArchiverException {
    if (!inputFile.isFile() || !inputFile.exists())
      throw new ArchiverException(inputFile.getAbsolutePath() + " isn't a file."); 
    if (this.replacePathSlashesToJavaPaths)
      destFileName = destFileName.replace('\\', '/'); 
    if (permissions < 0)
      permissions = getOverrideFileMode(); 
    try {
      ArchiveEntry entry = ArchiveEntry.createFileEntry(destFileName, inputFile, permissions, getDirectoryMode());
      doAddResource(updateArchiveEntryAttributes(entry));
    } catch (IOException e) {
      throw new ArchiverException("Failed to determine inclusion status for: " + inputFile, e);
    } 
  }
  
  @Nonnull
  public ResourceIterator getResources() throws ArchiverException {
    return new ResourceIterator() {
        private final Iterator<Object> addedResourceIter = AbstractArchiver.this.resources.iterator();
        
        private AbstractArchiver.AddedResourceCollection currentResourceCollection;
        
        private Iterator ioResourceIter;
        
        private ArchiveEntry nextEntry;
        
        private final Set<String> seenEntries = new HashSet<>();
        
        public boolean hasNext() {
          do {
            if (this.nextEntry == null)
              if (this.ioResourceIter == null) {
                if (this.addedResourceIter.hasNext()) {
                  Object o = this.addedResourceIter.next();
                  if (o instanceof ArchiveEntry) {
                    this.nextEntry = (ArchiveEntry)o;
                  } else if (o instanceof AbstractArchiver.AddedResourceCollection) {
                    this.currentResourceCollection = (AbstractArchiver.AddedResourceCollection)o;
                    try {
                      this.ioResourceIter = this.currentResourceCollection.resources.getResources();
                    } catch (IOException e) {
                      throw new ArchiverException(e.getMessage(), e);
                    } 
                  } else {
                    return throwIllegalResourceType(o);
                  } 
                } else {
                  this.nextEntry = null;
                } 
              } else if (this.ioResourceIter.hasNext()) {
                PlexusIoResource resource = this.ioResourceIter.next();
                this.nextEntry = AbstractArchiver.this.asArchiveEntry(this.currentResourceCollection, resource);
              } else {
                AbstractArchiver.this.addCloseable(this.ioResourceIter);
                this.ioResourceIter = null;
              }  
            if (this.nextEntry == null || !this.seenEntries.contains(normalizedForDuplicateCheck(this.nextEntry)))
              continue; 
            String path = this.nextEntry.getName();
            if ("preserve".equals(AbstractArchiver.this.duplicateBehavior) || "skip"
              .equals(AbstractArchiver.this.duplicateBehavior)) {
              if (this.nextEntry.getType() == 1)
                AbstractArchiver.this.getLogger().debug(path + " already added, skipping"); 
              this.nextEntry = null;
            } else {
              if ("fail".equals(AbstractArchiver.this.duplicateBehavior))
                throw new ArchiverException("Duplicate file " + path + " was found and the duplicate attribute is 'fail'."); 
              AbstractArchiver.this.getLogger().debug("duplicate file " + path + " found, adding.");
            } 
          } while (this.nextEntry == null && (this.ioResourceIter != null || this.addedResourceIter.hasNext()));
          return (this.nextEntry != null);
        }
        
        private boolean throwIllegalResourceType(Object o) {
          throw new IllegalStateException("An invalid resource of type: " + o
              .getClass().getName() + " was added to archiver: " + 
              getClass().getName());
        }
        
        public ArchiveEntry next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          ArchiveEntry next = this.nextEntry;
          this.nextEntry = null;
          this.seenEntries.add(normalizedForDuplicateCheck(next));
          return next;
        }
        
        public void remove() {
          throw new UnsupportedOperationException("Does not support iterator");
        }
        
        private String normalizedForDuplicateCheck(ArchiveEntry entry) {
          return entry.getName().replace('\\', '/');
        }
      };
  }
  
  private static void closeIfCloseable(Object resource) throws IOException {
    if (resource == null)
      return; 
    if (resource instanceof Closeable)
      ((Closeable)resource).close(); 
  }
  
  private static void closeQuietlyIfCloseable(Object resource) {
    try {
      closeIfCloseable(resource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public Map<String, ArchiveEntry> getFiles() {
    try {
      Map<String, ArchiveEntry> map = new HashMap<>();
      for (ResourceIterator iter = getResources(); iter.hasNext(); ) {
        ArchiveEntry entry = iter.next();
        if (this.includeEmptyDirs || entry.getType() == 1)
          map.put(entry.getName(), entry); 
      } 
      return map;
    } catch (ArchiverException e) {
      throw new UndeclaredThrowableException(e);
    } 
  }
  
  public File getDestFile() {
    return this.destFile;
  }
  
  public void setDestFile(File destFile) {
    this.destFile = destFile;
    if (destFile != null && destFile.getParentFile() != null)
      destFile.getParentFile().mkdirs(); 
  }
  
  protected PlexusIoResourceCollection asResourceCollection(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {
    PlexusIoResourceCollection resources;
    File archiveFile = fileSet.getArchive();
    try {
      resources = ((ArchiverManager)this.archiverManagerProvider.get()).getResourceCollection(archiveFile);
    } catch (NoSuchArchiverException e) {
      throw new ArchiverException("Error adding archived file-set. PlexusIoResourceCollection not found for: " + archiveFile, e);
    } 
    if (resources instanceof EncodingSupported)
      ((EncodingSupported)resources).setEncoding(charset); 
    if (resources instanceof PlexusIoArchivedResourceCollection) {
      ((PlexusIoArchivedResourceCollection)resources).setFile(fileSet.getArchive());
    } else {
      throw new ArchiverException("Expected " + PlexusIoArchivedResourceCollection.class.getName() + ", got " + resources
          .getClass().getName());
    } 
    if (resources instanceof AbstractPlexusIoResourceCollection)
      ((AbstractPlexusIoResourceCollection)resources).setStreamTransformer(fileSet.getStreamTransformer()); 
    PlexusIoProxyResourceCollection proxy = new PlexusIoProxyResourceCollection(resources);
    proxy.setExcludes(fileSet.getExcludes());
    proxy.setIncludes(fileSet.getIncludes());
    proxy.setIncludingEmptyDirectories(fileSet.isIncludingEmptyDirectories());
    proxy.setCaseSensitive(fileSet.isCaseSensitive());
    proxy.setPrefix(fileSet.getPrefix());
    proxy.setUsingDefaultExcludes(fileSet.isUsingDefaultExcludes());
    proxy.setFileSelectors(fileSet.getFileSelectors());
    proxy.setStreamTransformer(fileSet.getStreamTransformer());
    proxy.setFileMappers(fileSet.getFileMappers());
    if (getOverrideDirectoryMode() > -1 || getOverrideFileMode() > -1)
      proxy.setOverrideAttributes(-1, null, -1, null, getOverrideFileMode(), getOverrideDirectoryMode()); 
    if (getDefaultDirectoryMode() > -1 || getDefaultFileMode() > -1)
      proxy.setDefaultAttributes(-1, null, -1, null, getDefaultFileMode(), getDefaultDirectoryMode()); 
    return (PlexusIoResourceCollection)proxy;
  }
  
  public void addResources(PlexusIoResourceCollection collection) throws ArchiverException {
    doAddResource(new AddedResourceCollection(collection, this.forcedFileMode, this.forcedDirectoryMode));
  }
  
  private void doAddResource(Object item) {
    this.resources.add(item);
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet) throws ArchiverException {
    PlexusIoResourceCollection resourceCollection = asResourceCollection(fileSet, null);
    addResources(resourceCollection);
  }
  
  public void addArchivedFileSet(ArchivedFileSet fileSet, Charset charset) throws ArchiverException {
    PlexusIoResourceCollection resourceCollection = asResourceCollection(fileSet, charset);
    addResources(resourceCollection);
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix, String[] includes, String[] excludes) throws ArchiverException {
    addArchivedFileSet(
        (ArchivedFileSet)((DefaultArchivedFileSet)((DefaultArchivedFileSet)DefaultArchivedFileSet.archivedFileSet(archiveFile).prefixed(prefix)).includeExclude(includes, excludes)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String prefix) throws ArchiverException {
    addArchivedFileSet((ArchivedFileSet)((DefaultArchivedFileSet)DefaultArchivedFileSet.archivedFileSet(archiveFile).prefixed(prefix)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile, String[] includes, String[] excludes) throws ArchiverException {
    addArchivedFileSet(
        (ArchivedFileSet)((DefaultArchivedFileSet)DefaultArchivedFileSet.archivedFileSet(archiveFile).includeExclude(includes, excludes)).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public void addArchivedFileSet(@Nonnull File archiveFile) throws ArchiverException {
    addArchivedFileSet((ArchivedFileSet)DefaultArchivedFileSet.archivedFileSet(archiveFile).includeEmptyDirs(this.includeEmptyDirs));
  }
  
  public boolean isForced() {
    return this.forced;
  }
  
  public void setForced(boolean forced) {
    this.forced = forced;
  }
  
  public void addArchiveFinalizer(ArchiveFinalizer finalizer) {
    if (this.finalizers == null)
      this.finalizers = new ArrayList<>(); 
    this.finalizers.add(finalizer);
  }
  
  public void setArchiveFinalizers(List<ArchiveFinalizer> archiveFinalizers) {
    this.finalizers = archiveFinalizers;
  }
  
  public void setDotFileDirectory(File dotFileDirectory) {
    this.dotFileDirectory = dotFileDirectory;
  }
  
  protected boolean isUptodate() throws ArchiverException {
    File zipFile = getDestFile();
    if (!zipFile.exists()) {
      getLogger().debug("isUp2date: false (Destination " + zipFile.getPath() + " not found.)");
      return false;
    } 
    long destTimestamp = getFileLastModifiedTime(zipFile);
    Iterator<Object> it = this.resources.iterator();
    if (!it.hasNext()) {
      getLogger().debug("isUp2date: false (No input files.)");
      return false;
    } 
    while (it.hasNext()) {
      long l;
      Object o = it.next();
      if (o instanceof ArchiveEntry) {
        l = ((ArchiveEntry)o).getResource().getLastModified();
      } else if (o instanceof AddedResourceCollection) {
        try {
          l = ((AddedResourceCollection)o).resources.getLastModified();
        } catch (IOException e) {
          throw new ArchiverException(e.getMessage(), e);
        } 
      } else {
        throw new IllegalStateException("Invalid object type: " + o.getClass().getName());
      } 
      if (l == 0L) {
        getLogger().debug("isUp2date: false (Resource with unknown modification date found.)");
        return false;
      } 
      if (l > destTimestamp) {
        getLogger().debug("isUp2date: false (Resource with newer modification date found.)");
        return false;
      } 
    } 
    getLogger().debug("isUp2date: true");
    return true;
  }
  
  private long getFileLastModifiedTime(File file) throws ArchiverException {
    try {
      return Files.getLastModifiedTime(file.toPath(), new java.nio.file.LinkOption[0]).toMillis();
    } catch (IOException e) {
      throw new ArchiverException(e.getMessage(), e);
    } 
  }
  
  protected boolean checkForced() throws ArchiverException {
    if (!isForced() && isSupportingForced() && isUptodate()) {
      getLogger().debug("Archive " + getDestFile() + " is uptodate.");
      return false;
    } 
    return true;
  }
  
  public boolean isSupportingForced() {
    return false;
  }
  
  protected void runArchiveFinalizers() throws ArchiverException {
    if (this.finalizers != null)
      for (ArchiveFinalizer finalizer : this.finalizers)
        finalizer.finalizeArchiveCreation(this);  
  }
  
  public final void createArchive() throws ArchiverException, IOException {
    validate();
    try {
      try {
        if (this.dotFileDirectory != null)
          addArchiveFinalizer(new DotDirectiveArchiveFinalizer(this.dotFileDirectory)); 
        runArchiveFinalizers();
        execute();
      } finally {
        close();
      } 
    } catch (IOException e) {
      String msg = "Problem creating " + getArchiveType() + ": " + e.getMessage();
      StringBuffer revertBuffer = new StringBuffer();
      if (!revert(revertBuffer))
        msg = msg + revertBuffer.toString(); 
      throw new ArchiverException(msg, e);
    } finally {
      cleanUp();
    } 
    postCreateArchive();
  }
  
  protected boolean hasVirtualFiles() {
    if (this.finalizers != null)
      for (ArchiveFinalizer finalizer : this.finalizers) {
        List virtualFiles = finalizer.getVirtualFiles();
        if (virtualFiles != null && !virtualFiles.isEmpty())
          return true; 
      }  
    return false;
  }
  
  protected boolean revert(StringBuffer messageBuffer) {
    return true;
  }
  
  protected void validate() throws ArchiverException, IOException {}
  
  protected void postCreateArchive() throws ArchiverException, IOException {}
  
  private void addCloseable(Object maybeCloseable) {
    if (maybeCloseable instanceof Closeable)
      this.closeables.add((Closeable)maybeCloseable); 
  }
  
  private void closeIterators() {
    for (Closeable closeable : this.closeables)
      closeQuietlyIfCloseable(closeable); 
  }
  
  protected void cleanUp() throws IOException {
    closeIterators();
    for (Object resource : this.resources) {
      if (resource instanceof PlexusIoProxyResourceCollection)
        resource = ((PlexusIoProxyResourceCollection)resource).getSrc(); 
      closeIfCloseable(resource);
    } 
    this.resources.clear();
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
  
  public void setOverrideUid(int uid) {
    this.overrideUid = uid;
  }
  
  public void setOverrideUserName(String userName) {
    this.overrideUserName = userName;
  }
  
  public int getOverrideUid() {
    return this.overrideUid;
  }
  
  public String getOverrideUserName() {
    return this.overrideUserName;
  }
  
  public void setOverrideGid(int gid) {
    this.overrideGid = gid;
  }
  
  public void setOverrideGroupName(String groupName) {
    this.overrideGroupName = groupName;
  }
  
  public int getOverrideGid() {
    return this.overrideGid;
  }
  
  public String getOverrideGroupName() {
    return this.overrideGroupName;
  }
  
  @Deprecated
  public void configureReproducible(Date lastModifiedDate) {
    configureReproducibleBuild(FileTime.fromMillis(lastModifiedDate.getTime()));
  }
  
  public void configureReproducibleBuild(FileTime lastModifiedTime) {
    setLastModifiedTime(normalizeLastModifiedTime(lastModifiedTime));
    setFilenameComparator(String::compareTo);
    setFileMode(33188);
    setDirectoryMode(16877);
    setOverrideUid(0);
    setOverrideUserName("root");
    setOverrideGid(0);
    setOverrideGroupName("root");
  }
  
  protected FileTime normalizeLastModifiedTime(FileTime lastModifiedTime) {
    return lastModifiedTime;
  }
  
  protected abstract String getArchiveType();
  
  protected abstract void close() throws IOException;
  
  protected abstract void execute() throws ArchiverException, IOException;
}
