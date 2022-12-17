package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;
import javax.inject.Named;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.iq80.snappy.SnappyOutputStream;

@Named("tar")
public class TarArchiver extends AbstractArchiver {
  private boolean longWarningGiven = false;
  
  private TarLongFileMode longFileMode = TarLongFileMode.warn;
  
  private TarCompressionMethod compression = TarCompressionMethod.none;
  
  private final TarOptions options = new TarOptions();
  
  private TarArchiveOutputStream tOut;
  
  public void setLongfile(TarLongFileMode mode) {
    this.longFileMode = mode;
  }
  
  public void setCompression(TarCompressionMethod mode) {
    this.compression = mode;
  }
  
  protected void execute() throws ArchiverException, IOException {
    if (!checkForced())
      return; 
    ResourceIterator iter = getResources();
    if (!iter.hasNext())
      throw new EmptyArchiveException("archive cannot be empty"); 
    File tarFile = getDestFile();
    if (tarFile == null)
      throw new ArchiverException("You must set the destination tar file."); 
    if (tarFile.exists() && !tarFile.isFile())
      throw new ArchiverException(tarFile + " isn't a file."); 
    if (tarFile.exists() && !tarFile.canWrite())
      throw new ArchiverException(tarFile + " is read-only."); 
    getLogger().info("Building tar: " + tarFile.getAbsolutePath());
    try {
      this.tOut = new TarArchiveOutputStream(compress(this.compression, Files.newOutputStream(tarFile.toPath(), new java.nio.file.OpenOption[0])), "UTF8");
      if (this.longFileMode.isTruncateMode()) {
        this.tOut.setLongFileMode(1);
      } else if (this.longFileMode.isPosixMode() || this.longFileMode.isPosixWarnMode()) {
        this.tOut.setLongFileMode(3);
        this.tOut.setBigNumberMode(2);
      } else if (this.longFileMode.isFailMode() || this.longFileMode.isOmitMode()) {
        this.tOut.setLongFileMode(0);
      } else {
        this.tOut.setLongFileMode(2);
      } 
      this.longWarningGiven = false;
      while (iter.hasNext()) {
        ArchiveEntry entry = iter.next();
        if (ResourceUtils.isSame(entry.getResource(), tarFile))
          throw new ArchiverException("A tar file cannot include itself."); 
        String fileName = entry.getName();
        String name = StringUtils.replace(fileName, File.separatorChar, '/');
        tarFile(entry, this.tOut, name);
      } 
      this.tOut.close();
    } finally {
      IOUtil.close((OutputStream)this.tOut);
    } 
  }
  
  protected void tarFile(ArchiveEntry entry, TarArchiveOutputStream tOut, String vPath) throws ArchiverException, IOException {
    if (vPath.length() <= 0)
      return; 
    if (entry.getResource().isDirectory() && !vPath.endsWith("/"))
      vPath = vPath + "/"; 
    if (vPath.startsWith("/") && !this.options.getPreserveLeadingSlashes()) {
      int l = vPath.length();
      if (l <= 1)
        return; 
      vPath = vPath.substring(1, l);
    } 
    int pathLength = vPath.length();
    InputStream fIn = null;
    try {
      TarArchiveEntry te;
      if (!this.longFileMode.isGnuMode() && pathLength >= 100) {
        int maxPosixPathLen = 255;
        if (!this.longFileMode.isPosixMode())
          if (this.longFileMode.isPosixWarnMode()) {
            if (pathLength > maxPosixPathLen) {
              getLogger().warn("Entry: " + vPath + " longer than " + maxPosixPathLen + " characters.");
              if (!this.longWarningGiven) {
                getLogger().warn("Resulting tar file can only be processed successfully by GNU compatible tar commands");
                this.longWarningGiven = true;
              } 
            } 
          } else {
            if (this.longFileMode.isOmitMode()) {
              getLogger().info("Omitting: " + vPath);
              return;
            } 
            if (this.longFileMode.isWarnMode()) {
              getLogger().warn("Entry: " + vPath + " longer than " + 'd' + " characters.");
              if (!this.longWarningGiven) {
                getLogger().warn("Resulting tar file can only be processed successfully by GNU compatible tar commands");
                this.longWarningGiven = true;
              } 
            } else {
              if (this.longFileMode.isFailMode())
                throw new ArchiverException("Entry: " + vPath + " longer than " + 'd' + " characters."); 
              throw new IllegalStateException("Non gnu mode should never get here?");
            } 
          }  
      } 
      if (entry.getType() == 3) {
        SymlinkDestinationSupplier plexusIoSymlinkResource = (SymlinkDestinationSupplier)entry.getResource();
        te = new TarArchiveEntry(vPath, (byte)50);
        te.setLinkName(plexusIoSymlinkResource.getSymlinkDestination());
      } else {
        te = new TarArchiveEntry(vPath);
      } 
      if (getLastModifiedTime() == null) {
        long teLastModified = entry.getResource().getLastModified();
        te.setModTime((teLastModified == 0L) ? 
            System.currentTimeMillis() : 
            teLastModified);
      } else {
        te.setModTime(getLastModifiedTime().toMillis());
      } 
      if (entry.getType() == 3) {
        te.setSize(0L);
      } else if (!entry.getResource().isDirectory()) {
        long size = entry.getResource().getSize();
        te.setSize((size == -1L) ? 0L : size);
      } 
      te.setMode(entry.getMode());
      PlexusIoResourceAttributes attributes = entry.getResourceAttributes();
      te.setUserName((attributes != null && attributes.getUserName() != null) ? 
          attributes.getUserName() : 
          this.options.getUserName());
      te.setGroupName((attributes != null && attributes.getGroupName() != null) ? 
          attributes.getGroupName() : 
          this.options.getGroup());
      int userId = (attributes != null && attributes.getUserId() != null) ? attributes.getUserId().intValue() : this.options.getUid();
      if (userId >= 0)
        te.setUserId(userId); 
      int groupId = (attributes != null && attributes.getGroupId() != null) ? attributes.getGroupId().intValue() : this.options.getGid();
      if (groupId >= 0)
        te.setGroupId(groupId); 
      tOut.putArchiveEntry((ArchiveEntry)te);
      try {
        if (entry.getResource().isFile() && entry.getType() != 3) {
          fIn = entry.getInputStream();
          Streams.copyFullyDontCloseOutput(fIn, (OutputStream)tOut, "xAR");
        } 
      } catch (Throwable e) {
        getLogger().warn("When creating tar entry", e);
      } finally {
        tOut.closeArchiveEntry();
      } 
    } finally {
      IOUtil.close(fIn);
    } 
  }
  
  public class TarOptions {
    private String userName = "";
    
    private String groupName = "";
    
    private int uid;
    
    private int gid;
    
    private boolean preserveLeadingSlashes = false;
    
    public void setUserName(String userName) {
      this.userName = userName;
    }
    
    public String getUserName() {
      return this.userName;
    }
    
    public void setUid(int uid) {
      this.uid = uid;
    }
    
    public int getUid() {
      return this.uid;
    }
    
    public void setGroup(String groupName) {
      this.groupName = groupName;
    }
    
    public String getGroup() {
      return this.groupName;
    }
    
    public void setGid(int gid) {
      this.gid = gid;
    }
    
    public int getGid() {
      return this.gid;
    }
    
    public boolean getPreserveLeadingSlashes() {
      return this.preserveLeadingSlashes;
    }
    
    public void setPreserveLeadingSlashes(boolean preserveLeadingSlashes) {
      this.preserveLeadingSlashes = preserveLeadingSlashes;
    }
  }
  
  public enum TarCompressionMethod {
    none, gzip, bzip2, snappy, xz;
  }
  
  private OutputStream compress(TarCompressionMethod tarCompressionMethod, OutputStream ostream) throws IOException {
    if (TarCompressionMethod.gzip.equals(tarCompressionMethod))
      return Streams.bufferedOutputStream(new GZIPOutputStream(ostream)); 
    if (TarCompressionMethod.bzip2.equals(tarCompressionMethod))
      return (OutputStream)new BZip2CompressorOutputStream(Streams.bufferedOutputStream(ostream)); 
    if (TarCompressionMethod.snappy.equals(tarCompressionMethod))
      return (OutputStream)new SnappyOutputStream(Streams.bufferedOutputStream(ostream)); 
    if (TarCompressionMethod.xz.equals(tarCompressionMethod))
      return (OutputStream)new XZCompressorOutputStream(Streams.bufferedOutputStream(ostream)); 
    return ostream;
  }
  
  public boolean isSupportingForced() {
    return true;
  }
  
  protected void cleanUp() throws IOException {
    super.cleanUp();
    if (this.tOut != null)
      this.tOut.close(); 
  }
  
  protected void close() throws IOException {
    if (this.tOut != null)
      this.tOut.close(); 
  }
  
  protected String getArchiveType() {
    return "TAR";
  }
}
