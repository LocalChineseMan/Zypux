package org.codehaus.plexus.archiver.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.Manifest;
import javax.inject.Named;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.archiver.zip.ConcurrentJarCreator;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

@Named("jar")
public class JarArchiver extends ZipArchiver {
  private static final String META_INF_NAME = "META-INF";
  
  private static final String INDEX_NAME = "META-INF/INDEX.LIST";
  
  private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
  
  private Manifest configuredManifest;
  
  private Manifest savedConfiguredManifest;
  
  private Manifest filesetManifest;
  
  private Manifest originalManifest;
  
  private FilesetManifestConfig filesetManifestConfig;
  
  private boolean mergeManifestsMain = true;
  
  private Manifest manifest;
  
  private File manifestFile;
  
  private boolean index = false;
  
  private boolean createEmpty = false;
  
  private final List<String> rootEntries;
  
  private List<String> indexJars;
  
  private boolean minimalDefaultManifest = false;
  
  public JarArchiver() {
    this.archiveType = "jar";
    setEncoding("UTF8");
    this.rootEntries = new ArrayList<>();
  }
  
  public void setIndex(boolean flag) {
    this.index = flag;
  }
  
  public void setMinimalDefaultManifest(boolean minimalDefaultManifest) {
    this.minimalDefaultManifest = minimalDefaultManifest;
  }
  
  @Deprecated
  public void setManifestEncoding(String manifestEncoding) {}
  
  public void addConfiguredManifest(Manifest newManifest) throws ManifestException {
    if (this.configuredManifest == null) {
      this.configuredManifest = newManifest;
    } else {
      JdkManifestFactory.merge(this.configuredManifest, newManifest, false);
    } 
    this.savedConfiguredManifest = this.configuredManifest;
  }
  
  public void setManifest(File manifestFile) throws ArchiverException {
    if (!manifestFile.exists())
      throw new ArchiverException("Manifest file: " + manifestFile + " does not exist."); 
    this.manifestFile = manifestFile;
  }
  
  private Manifest getManifest(File manifestFile) throws ArchiverException {
    try {
      InputStream in = Streams.fileInputStream(manifestFile);
      try {
        Manifest manifest = getManifest(in);
        if (in != null)
          in.close(); 
        return manifest;
      } catch (Throwable throwable) {
        if (in != null)
          try {
            in.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException e) {
      throw new ArchiverException("Unable to read manifest file: " + manifestFile + " (" + e.getMessage() + ")", e);
    } 
  }
  
  private Manifest getManifest(InputStream is) throws ArchiverException {
    try {
      return new Manifest(is);
    } catch (IOException e) {
      throw new ArchiverException("Unable to read manifest file (" + e.getMessage() + ")", e);
    } 
  }
  
  public void setFilesetmanifest(FilesetManifestConfig config) {
    this.filesetManifestConfig = config;
    this.mergeManifestsMain = (FilesetManifestConfig.merge == config);
    if (this.filesetManifestConfig != null && this.filesetManifestConfig != FilesetManifestConfig.skip)
      this.doubleFilePass = true; 
  }
  
  public void addConfiguredIndexJars(File indexJar) {
    if (this.indexJars == null)
      this.indexJars = new ArrayList<>(); 
    this.indexJars.add(indexJar.getAbsolutePath());
  }
  
  protected void initZipOutputStream(ConcurrentJarCreator zOut) throws ArchiverException, IOException {
    if (!this.skipWriting) {
      Manifest jarManifest = createManifest();
      writeManifest(zOut, jarManifest);
    } 
  }
  
  protected boolean hasVirtualFiles() {
    getLogger().debug("\n\n\nChecking for jar manifest virtual files...\n\n\n");
    System.out.flush();
    return (this.configuredManifest != null || this.manifest != null || this.manifestFile != null || super
      .hasVirtualFiles());
  }
  
  protected Manifest createManifest() throws ArchiverException {
    Manifest finalManifest = Manifest.getDefaultManifest(this.minimalDefaultManifest);
    if (this.manifest == null && this.manifestFile != null)
      this.manifest = getManifest(this.manifestFile); 
    if (isInUpdateMode())
      JdkManifestFactory.merge(finalManifest, this.originalManifest, false); 
    JdkManifestFactory.merge(finalManifest, this.filesetManifest, false);
    JdkManifestFactory.merge(finalManifest, this.configuredManifest, false);
    JdkManifestFactory.merge(finalManifest, this.manifest, !this.mergeManifestsMain);
    return finalManifest;
  }
  
  private void writeManifest(ConcurrentJarCreator zOut, Manifest manifest) throws IOException, ArchiverException {
    for (Enumeration<String> e = manifest.getWarnings(); e.hasMoreElements();)
      getLogger().warn("Manifest warning: " + (String)e.nextElement()); 
    zipDir(null, zOut, "META-INF/", 16877, getEncoding());
    ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
    manifest.write(baos);
    InputStreamSupplier in = () -> new ByteArrayInputStream(baos.toByteArray());
    super.zipFile(in, zOut, "META-INF/MANIFEST.MF", System.currentTimeMillis(), null, 33188, null, false);
    super.initZipOutputStream(zOut);
  }
  
  protected void finalizeZipOutputStream(ConcurrentJarCreator zOut) throws IOException, ArchiverException {
    if (this.index)
      createIndexList(zOut); 
  }
  
  private void createIndexList(ConcurrentJarCreator zOut) throws IOException, ArchiverException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
    writer.println("JarIndex-Version: 1.0");
    writer.println();
    writer.println(getDestFile().getName());
    Set<String> filteredDirs = this.addedDirs.allAddedDirs();
    if (filteredDirs.contains("META-INF/")) {
      boolean add = false;
      for (String entry : this.entries.keySet()) {
        if (entry.startsWith("META-INF/") && !entry.equals("META-INF/INDEX.LIST") && 
          !entry.equals("META-INF/MANIFEST.MF")) {
          add = true;
          break;
        } 
      } 
      if (!add)
        filteredDirs.remove("META-INF/"); 
    } 
    writeIndexLikeList(new ArrayList<>(filteredDirs), this.rootEntries, writer);
    writer.println();
    if (this.indexJars != null) {
      Manifest mf = createManifest();
      String classpath = mf.getMainAttributes().getValue("Class-Path");
      String[] cpEntries = null;
      if (classpath != null) {
        StringTokenizer tok = new StringTokenizer(classpath, " ");
        cpEntries = new String[tok.countTokens()];
        int c = 0;
        while (tok.hasMoreTokens())
          cpEntries[c++] = tok.nextToken(); 
      } 
      for (String indexJar : this.indexJars) {
        String name = findJarName(indexJar, cpEntries);
        if (name != null) {
          List<String> dirs = new ArrayList<>();
          List<String> files = new ArrayList<>();
          grabFilesAndDirs(indexJar, dirs, files);
          if (dirs.size() + files.size() > 0) {
            writer.println(name);
            writeIndexLikeList(dirs, files, writer);
            writer.println();
          } 
        } 
      } 
    } 
    writer.flush();
    InputStreamSupplier in = () -> new ByteArrayInputStream(baos.toByteArray());
    super.zipFile(in, zOut, "META-INF/INDEX.LIST", System.currentTimeMillis(), null, 33188, null, true);
  }
  
  protected void zipFile(InputStreamSupplier is, ConcurrentJarCreator zOut, String vPath, long lastModified, File fromArchive, int mode, String symlinkDestination, boolean addInParallel) throws IOException, ArchiverException {
    if ("META-INF/MANIFEST.MF".equalsIgnoreCase(vPath)) {
      if (!this.doubleFilePass || this.skipWriting) {
        InputStream manifestInputStream = is.get();
        try {
          filesetManifest(fromArchive, manifestInputStream);
          if (manifestInputStream != null)
            manifestInputStream.close(); 
        } catch (Throwable throwable) {
          if (manifestInputStream != null)
            try {
              manifestInputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } 
    } else if ("META-INF/INDEX.LIST".equalsIgnoreCase(vPath) && this.index) {
      getLogger().warn("Warning: selected " + this.archiveType + " files include a META-INF/INDEX.LIST which will be replaced by a newly generated one.");
    } else {
      if (this.index && !vPath.contains("/"))
        this.rootEntries.add(vPath); 
      super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode, symlinkDestination, addInParallel);
    } 
  }
  
  private void filesetManifest(File file, InputStream is) throws ArchiverException {
    if (this.manifestFile != null && this.manifestFile.equals(file)) {
      getLogger().debug("Found manifest " + file);
      if (is != null) {
        this.manifest = getManifest(is);
      } else {
        this.manifest = getManifest(file);
      } 
    } else if (this.filesetManifestConfig != null && this.filesetManifestConfig != FilesetManifestConfig.skip) {
      Manifest newManifest;
      getLogger().debug("Found manifest to merge in file " + file);
      if (is != null) {
        newManifest = getManifest(is);
      } else {
        newManifest = getManifest(file);
      } 
      if (this.filesetManifest == null) {
        this.filesetManifest = newManifest;
      } else {
        JdkManifestFactory.merge(this.filesetManifest, newManifest, false);
      } 
    } 
  }
  
  protected boolean createEmptyZip(File zipFile) throws ArchiverException {
    if (!this.createEmpty)
      return true; 
    try {
      getLogger().debug("Building MANIFEST-only jar: " + getDestFile().getAbsolutePath());
      this
        .zipArchiveOutputStream = new ZipArchiveOutputStream(Streams.bufferedOutputStream(Streams.fileOutputStream(getDestFile(), "jar")));
      this.zipArchiveOutputStream.setEncoding(getEncoding());
      if (isCompress()) {
        this.zipArchiveOutputStream.setMethod(8);
      } else {
        this.zipArchiveOutputStream.setMethod(0);
      } 
      ConcurrentJarCreator ps = new ConcurrentJarCreator(isRecompressAddedZips(), Runtime.getRuntime().availableProcessors());
      initZipOutputStream(ps);
      finalizeZipOutputStream(ps);
    } catch (IOException ioe) {
      throw new ArchiverException("Could not create almost empty JAR archive (" + ioe.getMessage() + ")", ioe);
    } finally {
      this.createEmpty = false;
    } 
    return true;
  }
  
  protected void cleanUp() throws IOException {
    super.cleanUp();
    if (!this.doubleFilePass || !this.skipWriting) {
      this.manifest = null;
      this.configuredManifest = this.savedConfiguredManifest;
      this.filesetManifest = null;
      this.originalManifest = null;
    } 
    this.rootEntries.clear();
  }
  
  public void reset() {
    super.reset();
    this.configuredManifest = null;
    this.filesetManifestConfig = null;
    this.mergeManifestsMain = false;
    this.manifestFile = null;
    this.index = false;
  }
  
  public enum FilesetManifestConfig {
    skip, merge, mergewithoutmain;
  }
  
  protected final void writeIndexLikeList(List<String> dirs, List<String> files, PrintWriter writer) {
    Collections.sort(dirs);
    Collections.sort(files);
    for (String dir : dirs) {
      dir = dir.replace('\\', '/');
      if (dir.startsWith("./"))
        dir = dir.substring(2); 
      while (dir.startsWith("/"))
        dir = dir.substring(1); 
      int pos = dir.lastIndexOf('/');
      if (pos != -1)
        dir = dir.substring(0, pos); 
      writer.println(dir);
    } 
    for (String file : files)
      writer.println(file); 
  }
  
  protected static String findJarName(String fileName, String[] classpath) {
    if (classpath == null)
      return (new File(fileName)).getName(); 
    fileName = fileName.replace(File.separatorChar, '/');
    SortedMap<String, String> matches = new TreeMap<>(Comparator.<String>comparingInt(String::length).reversed());
    for (String aClasspath : classpath) {
      if (fileName.endsWith(aClasspath)) {
        matches.put(aClasspath, aClasspath);
      } else {
        int slash = aClasspath.indexOf("/");
        String candidate = aClasspath;
        while (slash > -1) {
          candidate = candidate.substring(slash + 1);
          if (fileName.endsWith(candidate)) {
            matches.put(candidate, aClasspath);
            break;
          } 
          slash = candidate.indexOf("/");
        } 
      } 
    } 
    return (matches.size() == 0) ? null : matches.get(matches.firstKey());
  }
  
  private void grabFilesAndDirs(String file, List<String> dirs, List<String> files) throws IOException {
    File zipFile = new File(file);
    if (!zipFile.exists()) {
      getLogger().error("JarArchive skipping non-existing file: " + zipFile.getAbsolutePath());
    } else if (zipFile.isDirectory()) {
      getLogger().info("JarArchiver skipping indexJar " + zipFile + " because it is not a jar");
    } else {
      ZipFile zf = new ZipFile(file, "utf-8");
      try {
        Enumeration<ZipArchiveEntry> entries = zf.getEntries();
        HashSet<String> dirSet = new HashSet<>();
        while (entries.hasMoreElements()) {
          ZipArchiveEntry ze = entries.nextElement();
          String name = ze.getName();
          if (!name.equals("META-INF") && !name.equals("META-INF/") && !name.equals("META-INF/INDEX.LIST") && 
            !name.equals("META-INF/MANIFEST.MF")) {
            if (ze.isDirectory()) {
              dirSet.add(name);
              continue;
            } 
            if (!name.contains("/")) {
              files.add(name);
              continue;
            } 
            dirSet.add(name.substring(0, name.lastIndexOf("/") + 1));
          } 
        } 
        dirs.addAll(dirSet);
        zf.close();
      } catch (Throwable throwable) {
        try {
          zf.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
  }
  
  protected void setZipEntryTime(ZipArchiveEntry zipEntry, long lastModifiedTime) {
    if (getLastModifiedTime() != null)
      lastModifiedTime = getLastModifiedTime().toMillis(); 
    zipEntry.setTime(lastModifiedTime);
  }
}
