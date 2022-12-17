package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import javax.annotation.Nonnull;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.CountingInputStream;
import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public abstract class AbstractZipUnArchiver extends AbstractUnArchiver {
  private static final String NATIVE_ENCODING = "native-encoding";
  
  private String encoding = "UTF8";
  
  private long maxOutputSize = Long.MAX_VALUE;
  
  public AbstractZipUnArchiver() {}
  
  public AbstractZipUnArchiver(File sourceFile) {
    super(sourceFile);
  }
  
  public void setEncoding(String encoding) {
    if ("native-encoding".equals(encoding))
      encoding = null; 
    this.encoding = encoding;
  }
  
  public void setMaxOutputSize(long maxOutputSize) {
    if (maxOutputSize <= 0L)
      throw new IllegalArgumentException("Invalid max output size specified: " + maxOutputSize); 
    this.maxOutputSize = maxOutputSize;
  }
  
  private static class ZipEntryFileInfo implements PlexusIoResource {
    private final ZipFile zipFile;
    
    private final ZipArchiveEntry zipEntry;
    
    ZipEntryFileInfo(ZipFile zipFile, ZipArchiveEntry zipEntry) {
      this.zipFile = zipFile;
      this.zipEntry = zipEntry;
    }
    
    public String getName() {
      return this.zipEntry.getName();
    }
    
    public boolean isDirectory() {
      return this.zipEntry.isDirectory();
    }
    
    public boolean isFile() {
      return (!this.zipEntry.isDirectory() && !this.zipEntry.isUnixSymlink());
    }
    
    public boolean isSymbolicLink() {
      return this.zipEntry.isUnixSymlink();
    }
    
    @Nonnull
    public InputStream getContents() throws IOException {
      return this.zipFile.getInputStream(this.zipEntry);
    }
    
    public long getLastModified() {
      long l = this.zipEntry.getTime();
      return (l == 0L) ? 0L : l;
    }
    
    public long getSize() {
      long l = this.zipEntry.getSize();
      return (l == -1L) ? -1L : l;
    }
    
    public URL getURL() throws IOException {
      return null;
    }
    
    public boolean isExisting() {
      return true;
    }
  }
  
  protected void execute() throws ArchiverException {
    execute("", getDestDirectory());
  }
  
  private String resolveSymlink(ZipFile zf, ZipArchiveEntry ze) throws IOException {
    if (ze.isUnixSymlink())
      return zf.getUnixSymlink(ze); 
    return null;
  }
  
  protected void execute(String path, File outputDirectory) throws ArchiverException {
    getLogger().debug("Expanding: " + getSourceFile() + " into " + outputDirectory);
    try {
      ZipFile zipFile = new ZipFile(getSourceFile(), this.encoding, true);
      try {
        long remainingSpace = this.maxOutputSize;
        Enumeration<ZipArchiveEntry> e = zipFile.getEntriesInPhysicalOrder();
        while (e.hasMoreElements()) {
          ZipArchiveEntry ze = e.nextElement();
          ZipEntryFileInfo fileInfo = new ZipEntryFileInfo(zipFile, ze);
          if (!isSelected(ze.getName(), fileInfo))
            continue; 
          if (ze.getName().startsWith(path)) {
            InputStream in = zipFile.getInputStream(ze);
            try {
              BoundedInputStream bis = new BoundedInputStream(in, remainingSpace + 1L);
              CountingInputStream cis = new CountingInputStream((InputStream)bis);
              extractFile(getSourceFile(), outputDirectory, (InputStream)cis, ze
                  .getName(), new Date(ze.getTime()), ze.isDirectory(), 
                  (ze.getUnixMode() != 0) ? Integer.valueOf(ze.getUnixMode()) : null, 
                  resolveSymlink(zipFile, ze), getFileMappers());
              remainingSpace -= cis.getByteCount();
              if (remainingSpace < 0L)
                throw new ArchiverException("Maximum output size limit reached"); 
              if (in != null)
                in.close(); 
            } catch (Throwable throwable) {
              if (in != null)
                try {
                  in.close();
                } catch (Throwable throwable1) {
                  throwable.addSuppressed(throwable1);
                }  
              throw throwable;
            } 
          } 
        } 
        getLogger().debug("expand complete");
        zipFile.close();
      } catch (Throwable throwable) {
        try {
          zipFile.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException ioe) {
      throw new ArchiverException("Error while expanding " + getSourceFile().getAbsolutePath(), ioe);
    } 
  }
}
