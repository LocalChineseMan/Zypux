package org.codehaus.plexus.archiver.zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.exceptions.EmptyArchiveException;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.FileUtils;

public abstract class AbstractZipArchiver extends AbstractArchiver {
  private String comment;
  
  private String encoding = "UTF8";
  
  private boolean doCompress = true;
  
  private boolean recompressAddedZips = true;
  
  private boolean doUpdate = false;
  
  private boolean savedDoUpdate = false;
  
  protected String archiveType = "zip";
  
  private boolean doFilesonly = false;
  
  protected final Hashtable<String, String> entries = new Hashtable<>();
  
  protected final AddedDirs addedDirs = new AddedDirs();
  
  private static final long EMPTY_CRC = (new CRC32()).getValue();
  
  protected boolean doubleFilePass = false;
  
  protected boolean skipWriting = false;
  
  @Deprecated
  protected final String duplicate = "skip";
  
  protected boolean addingNewFiles = false;
  
  private File renamedFile = null;
  
  private File zipFile;
  
  private boolean success;
  
  private ConcurrentJarCreator zOut;
  
  protected ZipArchiveOutputStream zipArchiveOutputStream;
  
  public String getComment() {
    return this.comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getEncoding() {
    return this.encoding;
  }
  
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
  
  public void setCompress(boolean compress) {
    this.doCompress = compress;
  }
  
  public boolean isCompress() {
    return this.doCompress;
  }
  
  public boolean isRecompressAddedZips() {
    return this.recompressAddedZips;
  }
  
  public void setRecompressAddedZips(boolean recompressAddedZips) {
    this.recompressAddedZips = recompressAddedZips;
  }
  
  public void setUpdateMode(boolean update) {
    this.doUpdate = update;
    this.savedDoUpdate = this.doUpdate;
  }
  
  public boolean isInUpdateMode() {
    return this.doUpdate;
  }
  
  public void setFilesonly(boolean f) {
    this.doFilesonly = f;
  }
  
  public boolean isFilesonly() {
    return this.doFilesonly;
  }
  
  protected void execute() throws ArchiverException, IOException {
    if (!checkForced())
      return; 
    if (this.doubleFilePass) {
      this.skipWriting = true;
      createArchiveMain();
      this.skipWriting = false;
      createArchiveMain();
    } else {
      createArchiveMain();
    } 
    finalizeZipOutputStream(this.zOut);
  }
  
  protected void finalizeZipOutputStream(ConcurrentJarCreator zOut) throws IOException, ArchiverException {}
  
  private void createArchiveMain() throws ArchiverException, IOException {
    if (!"skip".equals("skip"))
      setDuplicateBehavior("skip"); 
    ResourceIterator iter = getResources();
    if (!iter.hasNext() && !hasVirtualFiles())
      throw new EmptyArchiveException("archive cannot be empty"); 
    this.zipFile = getDestFile();
    if (this.zipFile == null)
      throw new ArchiverException("You must set the destination " + this.archiveType + "file."); 
    if (this.zipFile.exists() && !this.zipFile.isFile())
      throw new ArchiverException(this.zipFile + " isn't a file."); 
    if (this.zipFile.exists() && !this.zipFile.canWrite())
      throw new ArchiverException(this.zipFile + " is read-only."); 
    this.addingNewFiles = true;
    if (this.doUpdate && !this.zipFile.exists()) {
      this.doUpdate = false;
      getLogger().debug("ignoring update attribute as " + this.archiveType + " doesn't exist.");
    } 
    this.success = false;
    if (this.doUpdate) {
      this.renamedFile = FileUtils.createTempFile("zip", ".tmp", this.zipFile.getParentFile());
      this.renamedFile.deleteOnExit();
      try {
        FileUtils.rename(this.zipFile, this.renamedFile);
      } catch (SecurityException e) {
        getLogger().debug(e.toString());
        throw new ArchiverException("Not allowed to rename old file (" + this.zipFile
            .getAbsolutePath() + ") to temporary file", e);
      } catch (IOException e) {
        getLogger().debug(e.toString());
        throw new ArchiverException("Unable to rename old file (" + this.zipFile
            .getAbsolutePath() + ") to temporary file", e);
      } 
    } 
    String action = this.doUpdate ? "Updating " : "Building ";
    getLogger().info(action + this.archiveType + ": " + this.zipFile.getAbsolutePath());
    if (!this.skipWriting) {
      this
        .zipArchiveOutputStream = new ZipArchiveOutputStream(Streams.bufferedOutputStream(Streams.fileOutputStream(this.zipFile, "zip")));
      this.zipArchiveOutputStream.setEncoding(this.encoding);
      this.zipArchiveOutputStream.setCreateUnicodeExtraFields(getUnicodeExtraFieldPolicy());
      this.zipArchiveOutputStream.setMethod(
          this.doCompress ? 8 : 0);
      this.zOut = new ConcurrentJarCreator(this.recompressAddedZips, Runtime.getRuntime().availableProcessors());
    } 
    initZipOutputStream(this.zOut);
    addResources(iter, this.zOut);
    if (this.doUpdate)
      if (!this.renamedFile.delete())
        getLogger().warn("Warning: unable to delete temporary file " + this.renamedFile.getName());  
    this.success = true;
  }
  
  private ZipArchiveOutputStream.UnicodeExtraFieldPolicy getUnicodeExtraFieldPolicy() {
    String effectiveEncoding = getEncoding();
    if (effectiveEncoding == null)
      effectiveEncoding = Charset.defaultCharset().name(); 
    boolean utf8 = StandardCharsets.UTF_8.name().equalsIgnoreCase(effectiveEncoding);
    if (!utf8)
      for (String alias : StandardCharsets.UTF_8.aliases()) {
        if (alias.equalsIgnoreCase(effectiveEncoding)) {
          utf8 = true;
          break;
        } 
      }  
    return utf8 ? 
      ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NEVER : 
      ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS;
  }
  
  protected final void addResources(ResourceIterator resources, ConcurrentJarCreator zOut) throws IOException, ArchiverException {
    while (resources.hasNext()) {
      ArchiveEntry entry = resources.next();
      String name = entry.getName();
      name = name.replace(File.separatorChar, '/');
      if ("".equals(name))
        continue; 
      if (entry.getResource().isDirectory() && !name.endsWith("/"))
        name = name + "/"; 
      addParentDirs(entry, (File)null, name, zOut);
      if (entry.getResource().isFile()) {
        zipFile(entry, zOut, name);
        continue;
      } 
      zipDir(entry.getResource(), zOut, name, entry.getMode(), this.encoding);
    } 
  }
  
  private void addParentDirs(ArchiveEntry archiveEntry, File baseDir, String entry, ConcurrentJarCreator zOut) throws IOException {
    if (!this.doFilesonly && getIncludeEmptyDirs()) {
      Deque<String> directories = this.addedDirs.asStringDeque(entry);
      while (!directories.isEmpty()) {
        File f;
        String dir = directories.pop();
        if (baseDir != null) {
          f = new File(baseDir, dir);
        } else {
          f = new File(dir);
        } 
        AnonymousResource anonymousResource = new AnonymousResource(f);
        zipDir((PlexusIoResource)anonymousResource, zOut, dir, archiveEntry.getDefaultDirMode(), this.encoding);
      } 
    } 
  }
  
  protected void zipFile(InputStreamSupplier in, ConcurrentJarCreator zOut, String vPath, long lastModified, File fromArchive, int mode, String symlinkDestination, boolean addInParallel) throws IOException, ArchiverException {
    getLogger().debug("adding entry " + vPath);
    this.entries.put(vPath, vPath);
    if (!this.skipWriting) {
      ZipArchiveEntry ze = new ZipArchiveEntry(vPath);
      setZipEntryTime(ze, lastModified);
      ze.setMethod(this.doCompress ? 8 : 0);
      ze.setUnixMode(0x8000 | mode);
      if (ze.isUnixSymlink()) {
        byte[] bytes = encodeArchiveEntry(symlinkDestination, getEncoding());
        InputStreamSupplier payload = () -> new ByteArrayInputStream(bytes);
        zOut.addArchiveEntry(ze, payload, true);
      } else {
        zOut.addArchiveEntry(ze, in, addInParallel);
      } 
    } 
  }
  
  protected void zipFile(ArchiveEntry entry, ConcurrentJarCreator zOut, String vPath) throws IOException, ArchiverException {
    PlexusIoResource resource = entry.getResource();
    if (ResourceUtils.isSame(resource, getDestFile()))
      throw new ArchiverException("A zip file cannot include itself"); 
    boolean b = entry.getResource() instanceof SymlinkDestinationSupplier;
    String symlinkTarget = b ? ((SymlinkDestinationSupplier)entry.getResource()).getSymlinkDestination() : null;
    InputStreamSupplier in = () -> {
        try {
          return entry.getInputStream();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        } 
      };
    try {
      zipFile(in, zOut, vPath, resource.getLastModified(), (File)null, entry.getMode(), symlinkTarget, 
          !entry.shouldAddSynchronously());
    } catch (IOException e) {
      throw new ArchiverException("IOException when zipping r" + entry.getName() + ": " + e.getMessage(), e);
    } 
  }
  
  protected void setZipEntryTime(ZipArchiveEntry zipEntry, long lastModifiedTime) {
    if (getLastModifiedTime() != null)
      lastModifiedTime = getLastModifiedTime().toMillis(); 
    zipEntry.setTime(lastModifiedTime + 1999L);
  }
  
  protected void zipDir(PlexusIoResource dir, ConcurrentJarCreator zOut, String vPath, int mode, String encodingToUse) throws IOException {
    if (this.addedDirs.update(vPath))
      return; 
    getLogger().debug("adding directory " + vPath);
    if (!this.skipWriting) {
      boolean isSymlink = dir instanceof SymlinkDestinationSupplier;
      if (isSymlink && vPath.endsWith(File.separator))
        vPath = vPath.substring(0, vPath.length() - 1); 
      ZipArchiveEntry ze = new ZipArchiveEntry(vPath);
      if (isSymlink)
        mode = 0xA000 | mode; 
      if (dir != null && dir.isExisting()) {
        setZipEntryTime(ze, dir.getLastModified());
      } else {
        setZipEntryTime(ze, System.currentTimeMillis());
      } 
      if (!isSymlink) {
        ze.setSize(0L);
        ze.setMethod(0);
        ze.setCrc(EMPTY_CRC);
      } 
      ze.setUnixMode(mode);
      if (!isSymlink) {
        zOut.addArchiveEntry(ze, () -> Streams.EMPTY_INPUTSTREAM, true);
      } else {
        String symlinkDestination = ((SymlinkDestinationSupplier)dir).getSymlinkDestination();
        byte[] bytes = encodeArchiveEntry(symlinkDestination, encodingToUse);
        ze.setMethod(8);
        zOut.addArchiveEntry(ze, () -> new ByteArrayInputStream(bytes), true);
      } 
    } 
  }
  
  private byte[] encodeArchiveEntry(String payload, String encoding) throws IOException {
    ZipEncoding enc = ZipEncodingHelper.getZipEncoding(encoding);
    ByteBuffer encodedPayloadByteBuffer = enc.encode(payload);
    byte[] encodedPayloadBytes = new byte[encodedPayloadByteBuffer.limit()];
    encodedPayloadByteBuffer.get(encodedPayloadBytes);
    return encodedPayloadBytes;
  }
  
  protected boolean createEmptyZip(File zipFile) throws ArchiverException {
    getLogger().info("Note: creating empty " + this.archiveType + " archive " + zipFile);
    try {
      OutputStream os = Files.newOutputStream(zipFile.toPath(), new java.nio.file.OpenOption[0]);
      try {
        byte[] empty = new byte[22];
        empty[0] = 80;
        empty[1] = 75;
        empty[2] = 5;
        empty[3] = 6;
        os.write(empty);
        if (os != null)
          os.close(); 
      } catch (Throwable throwable) {
        if (os != null)
          try {
            os.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException ioe) {
      throw new ArchiverException("Could not create empty ZIP archive (" + ioe.getMessage() + ")", ioe);
    } 
    return true;
  }
  
  protected void cleanUp() throws IOException {
    super.cleanUp();
    this.addedDirs.clear();
    this.entries.clear();
    this.addingNewFiles = false;
    this.doUpdate = this.savedDoUpdate;
    this.success = false;
    this.zOut = null;
    this.renamedFile = null;
    this.zipFile = null;
  }
  
  public void reset() {
    setDestFile(null);
    this.archiveType = "zip";
    this.doCompress = true;
    this.doUpdate = false;
    this.doFilesonly = false;
    this.encoding = null;
  }
  
  protected void initZipOutputStream(ConcurrentJarCreator zOut) throws ArchiverException, IOException {}
  
  public boolean isSupportingForced() {
    return true;
  }
  
  protected boolean revert(StringBuffer messageBuffer) {
    int initLength = messageBuffer.length();
    if ((!this.doUpdate || this.renamedFile != null) && !this.zipFile.delete())
      messageBuffer.append(" (and the archive is probably corrupt but I could not delete it)"); 
    if (this.doUpdate && this.renamedFile != null)
      try {
        FileUtils.rename(this.renamedFile, this.zipFile);
      } catch (IOException e) {
        messageBuffer.append(" (and I couldn't rename the temporary file ");
        messageBuffer.append(this.renamedFile.getName());
        messageBuffer.append(" back)");
      }  
    return (messageBuffer.length() == initLength);
  }
  
  protected void close() throws IOException {
    try {
      if (this.zipArchiveOutputStream != null) {
        if (this.zOut != null)
          this.zOut.writeTo(this.zipArchiveOutputStream); 
        this.zipArchiveOutputStream.close();
      } 
    } catch (IOException ex) {
      if (this.success)
        throw ex; 
    } catch (InterruptedException e) {
      IOException ex = new IOException("InterruptedException exception");
      ex.initCause(e.getCause());
      throw ex;
    } catch (ExecutionException e) {
      IOException ex = new IOException("Execution exception");
      ex.initCause(e.getCause());
      throw ex;
    } 
  }
  
  protected String getArchiveType() {
    return this.archiveType;
  }
  
  protected FileTime normalizeLastModifiedTime(FileTime lastModifiedTime) {
    return FileTime.fromMillis(dosToJavaTime(lastModifiedTime.toMillis()));
  }
  
  private static long dosToJavaTime(long dosTime) {
    Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.ROOT);
    cal.setTimeInMillis(dosTime);
    return dosTime - (cal.get(15) + cal.get(16));
  }
}
