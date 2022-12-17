package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUnArchiver implements UnArchiver, FinalizerEnabled {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  private File destDirectory;
  
  private File destFile;
  
  private File sourceFile;
  
  protected Logger getLogger() {
    return this.logger;
  }
  
  private boolean overwrite = true;
  
  private FileMapper[] fileMappers;
  
  private List<ArchiveFinalizer> finalizers;
  
  private FileSelector[] fileSelectors;
  
  private boolean useJvmChmod = true;
  
  private boolean ignorePermissions = false;
  
  final AtomicInteger casingMessageEmitted;
  
  public File getDestDirectory() {
    return this.destDirectory;
  }
  
  public void setDestDirectory(File destDirectory) {
    this.destDirectory = destDirectory;
  }
  
  public File getDestFile() {
    return this.destFile;
  }
  
  public void setDestFile(File destFile) {
    this.destFile = destFile;
  }
  
  public File getSourceFile() {
    return this.sourceFile;
  }
  
  public void setSourceFile(File sourceFile) {
    this.sourceFile = sourceFile;
  }
  
  public boolean isOverwrite() {
    return this.overwrite;
  }
  
  public void setOverwrite(boolean b) {
    this.overwrite = b;
  }
  
  public FileMapper[] getFileMappers() {
    return this.fileMappers;
  }
  
  public void setFileMappers(FileMapper[] fileMappers) {
    this.fileMappers = fileMappers;
  }
  
  public final void extract() throws ArchiverException {
    validate();
    execute();
    runArchiveFinalizers();
  }
  
  public final void extract(String path, File outputDirectory) throws ArchiverException {
    validate(path, outputDirectory);
    execute(path, outputDirectory);
    runArchiveFinalizers();
  }
  
  public void addArchiveFinalizer(ArchiveFinalizer finalizer) {
    if (this.finalizers == null)
      this.finalizers = new ArrayList<>(); 
    this.finalizers.add(finalizer);
  }
  
  public void setArchiveFinalizers(List<ArchiveFinalizer> archiveFinalizers) {
    this.finalizers = archiveFinalizers;
  }
  
  private void runArchiveFinalizers() throws ArchiverException {
    if (this.finalizers != null)
      for (ArchiveFinalizer finalizer : this.finalizers)
        finalizer.finalizeArchiveExtraction(this);  
  }
  
  protected void validate(String path, File outputDirectory) {}
  
  protected void validate() throws ArchiverException {
    if (this.sourceFile == null)
      throw new ArchiverException("The source file isn't defined."); 
    if (this.sourceFile.isDirectory())
      throw new ArchiverException("The source must not be a directory."); 
    if (!this.sourceFile.exists())
      throw new ArchiverException("The source file " + this.sourceFile + " doesn't exist."); 
    if (this.destDirectory == null && this.destFile == null)
      throw new ArchiverException("The destination isn't defined."); 
    if (this.destDirectory != null && this.destFile != null)
      throw new ArchiverException("You must choose between a destination directory and a destination file."); 
    if (this.destDirectory != null && !this.destDirectory.isDirectory()) {
      this.destFile = this.destDirectory;
      this.destDirectory = null;
    } 
    if (this.destFile != null && this.destFile.isDirectory()) {
      this.destDirectory = this.destFile;
      this.destFile = null;
    } 
  }
  
  public void setFileSelectors(FileSelector[] fileSelectors) {
    this.fileSelectors = fileSelectors;
  }
  
  public FileSelector[] getFileSelectors() {
    return this.fileSelectors;
  }
  
  protected boolean isSelected(String fileName, PlexusIoResource fileInfo) throws ArchiverException {
    if (this.fileSelectors != null)
      for (FileSelector fileSelector : this.fileSelectors) {
        try {
          if (!fileSelector.isSelected((FileInfo)fileInfo))
            return false; 
        } catch (IOException e) {
          throw new ArchiverException("Failed to check, whether " + fileInfo
              .getName() + " is selected: " + e.getMessage(), e);
        } 
      }  
    return true;
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
  
  protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, Integer mode, String symlinkDestination, FileMapper[] fileMappers) throws IOException, ArchiverException {
    if (fileMappers != null)
      for (FileMapper fileMapper : fileMappers)
        entryName = fileMapper.getMappedFileName(entryName);  
    File targetFileName = FileUtils.resolveFile(dir, entryName);
    String canonicalDirPath = dir.getCanonicalPath();
    String canonicalDestPath = targetFileName.getCanonicalPath();
    if (!canonicalDestPath.startsWith(canonicalDirPath))
      throw new ArchiverException("Entry is outside of the target directory (" + entryName + ")"); 
    try {
      if (!shouldExtractEntry(dir, targetFileName, entryName, entryDate))
        return; 
      File dirF = targetFileName.getParentFile();
      if (dirF != null)
        dirF.mkdirs(); 
      if (!StringUtils.isEmpty(symlinkDestination)) {
        SymlinkUtils.createSymbolicLink(targetFileName, new File(symlinkDestination));
      } else if (isDirectory) {
        targetFileName.mkdirs();
      } else {
        OutputStream out = Files.newOutputStream(targetFileName.toPath(), new java.nio.file.OpenOption[0]);
        try {
          IOUtil.copy(compressedInputStream, out);
          if (out != null)
            out.close(); 
        } catch (Throwable throwable) {
          if (out != null)
            try {
              out.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } 
      targetFileName.setLastModified(entryDate.getTime());
      if (!isIgnorePermissions() && mode != null && !isDirectory)
        ArchiveEntryUtils.chmod(targetFileName, mode.intValue()); 
    } catch (FileNotFoundException ex) {
      getLogger().warn("Unable to expand to file " + targetFileName.getPath());
    } 
  }
  
  public AbstractUnArchiver() {
    this.casingMessageEmitted = new AtomicInteger(0);
  }
  
  public AbstractUnArchiver(File sourceFile) {
    this.casingMessageEmitted = new AtomicInteger(0);
    this.sourceFile = sourceFile;
  }
  
  protected boolean shouldExtractEntry(File targetDirectory, File targetFileName, String entryName, Date entryDate) throws IOException {
    if (!targetFileName.exists())
      return true; 
    boolean entryIsDirectory = entryName.endsWith("/");
    String canonicalDestPath = targetFileName.getCanonicalPath();
    String suffix = entryIsDirectory ? "/" : "";
    String relativeCanonicalDestPath = canonicalDestPath.replace(targetDirectory
        .getCanonicalPath() + File.separatorChar, "") + suffix;
    boolean fileOnDiskIsNewerThanEntry = (targetFileName.lastModified() >= entryDate.getTime());
    boolean differentCasing = !entryName.equals(relativeCanonicalDestPath);
    String casingMessage = String.format(Locale.ENGLISH, "Archive entry '%s' and existing file '%s' names differ only by case. This may lead to an unexpected outcome on case-insensitive filesystems.", new Object[] { entryName, canonicalDestPath });
    if (fileOnDiskIsNewerThanEntry) {
      if (differentCasing) {
        getLogger().warn(casingMessage);
        this.casingMessageEmitted.incrementAndGet();
      } 
      return false;
    } 
    if (differentCasing) {
      getLogger().warn(casingMessage);
      this.casingMessageEmitted.incrementAndGet();
    } 
    return isOverwrite();
  }
  
  protected abstract void execute() throws ArchiverException;
  
  protected abstract void execute(String paramString, File paramFile) throws ArchiverException;
}
