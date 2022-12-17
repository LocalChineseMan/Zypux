package org.codehaus.plexus.archiver.dir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

@Named("dir")
public class DirectoryArchiver extends AbstractArchiver {
  private final List<Runnable> directoryChmods = new ArrayList<>();
  
  public void resetArchiver() throws IOException {
    cleanUp();
  }
  
  public void execute() throws ArchiverException, IOException {
    ResourceIterator iter = getResources();
    if (!iter.hasNext())
      throw new EmptyArchiveException("archive cannot be empty"); 
    File destDirectory = getDestFile();
    if (destDirectory == null)
      throw new ArchiverException("You must set the destination directory."); 
    if (destDirectory.exists() && !destDirectory.isDirectory())
      throw new ArchiverException(destDirectory + " is not a directory."); 
    if (destDirectory.exists() && !destDirectory.canWrite())
      throw new ArchiverException(destDirectory + " is not writable."); 
    getLogger().info("Copying files to " + destDirectory.getAbsolutePath());
    try {
      while (iter.hasNext()) {
        ArchiveEntry f = iter.next();
        if (ResourceUtils.isSame(f.getResource(), destDirectory))
          throw new ArchiverException("The destination directory cannot include itself."); 
        String fileName = f.getName();
        String destDir = destDirectory.getCanonicalPath();
        fileName = destDir + File.separator + fileName;
        PlexusIoResource resource = f.getResource();
        if (resource instanceof SymlinkDestinationSupplier) {
          String dest = ((SymlinkDestinationSupplier)resource).getSymlinkDestination();
          File target = new File(dest);
          File symlink = new File(fileName);
          makeParentDirectories(symlink);
          SymlinkUtils.createSymbolicLink(symlink, target);
          continue;
        } 
        copyFile(f, fileName);
      } 
      this.directoryChmods.forEach(Runnable::run);
      this.directoryChmods.clear();
    } catch (IOException ioe) {
      String message = "Problem copying files : " + ioe.getMessage();
      throw new ArchiverException(message, ioe);
    } 
  }
  
  protected void copyFile(ArchiveEntry entry, String vPath) throws ArchiverException, IOException {
    if (vPath.length() <= 0)
      return; 
    PlexusIoResource in = entry.getResource();
    File outFile = new File(vPath);
    long inLastModified = in.getLastModified();
    long outLastModified = outFile.lastModified();
    if (ResourceUtils.isUptodate(inLastModified, outLastModified))
      return; 
    if (!in.isDirectory()) {
      makeParentDirectories(outFile);
      ResourceUtils.copyFile(entry.getInputStream(), outFile);
      setFileModes(entry, outFile, inLastModified);
    } else {
      if (outFile.exists()) {
        if (!outFile.isDirectory())
          throw new ArchiverException("Expected directory and found file at copy destination of " + in
              .getName() + " to " + outFile); 
      } else if (!outFile.mkdirs()) {
        throw new ArchiverException("Unable to create directory or parent directory of " + outFile);
      } 
      this.directoryChmods.add(() -> {
            try {
              setFileModes(entry, outFile, inLastModified);
            } catch (IOException e) {
              throw new ArchiverException("Failed setting file attributes", e);
            } 
          });
    } 
  }
  
  private static void makeParentDirectories(File file) {
    if (!file.getParentFile().exists())
      if (!file.getParentFile().mkdirs())
        throw new ArchiverException("Unable to create directory or parent directory of " + file);  
  }
  
  private void setFileModes(ArchiveEntry entry, File outFile, long inLastModified) throws IOException {
    if (!isIgnorePermissions())
      ArchiveEntryUtils.chmod(outFile, entry.getMode()); 
    if (getLastModifiedTime() == null) {
      FileTime fromMillis = FileTime.fromMillis((inLastModified == 0L) ? 
          System.currentTimeMillis() : 
          inLastModified);
      Files.setLastModifiedTime(outFile.toPath(), fromMillis);
    } else {
      Files.setLastModifiedTime(outFile.toPath(), getLastModifiedTime());
    } 
  }
  
  protected void cleanUp() throws IOException {
    super.cleanUp();
    setIncludeEmptyDirs(false);
    setIncludeEmptyDirs(true);
  }
  
  protected void close() throws IOException {}
  
  protected String getArchiveType() {
    return "directory";
  }
}
