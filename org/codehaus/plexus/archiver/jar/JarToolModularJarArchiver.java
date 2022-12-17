package org.codehaus.plexus.archiver.jar;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.inject.Named;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.io.output.NullPrintStream;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ConcurrentJarCreator;
import org.codehaus.plexus.util.IOUtil;

@Named("mjar")
public class JarToolModularJarArchiver extends ModularJarArchiver {
  private static final String MODULE_DESCRIPTOR_FILE_NAME = "module-info.class";
  
  private static final Pattern MRJAR_VERSION_AREA = Pattern.compile("META-INF/versions/\\d+/");
  
  private Object jarTool;
  
  private boolean moduleDescriptorFound;
  
  private boolean hasJarDateOption;
  
  public JarToolModularJarArchiver() {
    try {
      Class<?> toolProviderClass = Class.forName("java.util.spi.ToolProvider");
      Object jarToolOptional = toolProviderClass.getMethod("findFirst", new Class[] { String.class }).invoke(null, new Object[] { "jar" });
      this
        .jarTool = jarToolOptional.getClass().getMethod("get", new Class[0]).invoke(jarToolOptional, new Object[0]);
    } catch (ReflectiveOperationException|SecurityException reflectiveOperationException) {}
  }
  
  protected void zipFile(InputStreamSupplier is, ConcurrentJarCreator zOut, String vPath, long lastModified, File fromArchive, int mode, String symlinkDestination, boolean addInParallel) throws IOException, ArchiverException {
    if (this.jarTool != null && isModuleDescriptor(vPath)) {
      getLogger().debug("Module descriptor found: " + vPath);
      this.moduleDescriptorFound = true;
    } 
    super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode, symlinkDestination, addInParallel);
  }
  
  protected void postCreateArchive() throws ArchiverException {
    if (!this.moduleDescriptorFound)
      return; 
    try {
      getLogger().debug("Using the jar tool to update the archive to modular JAR.");
      Method jarRun = this.jarTool.getClass().getMethod("run", new Class[] { PrintStream.class, PrintStream.class, String[].class });
      if (getLastModifiedTime() != null) {
        this.hasJarDateOption = isJarDateOptionSupported(jarRun);
        getLogger().debug("jar tool --date option is supported: " + this.hasJarDateOption);
      } 
      Integer result = (Integer)jarRun.invoke(this.jarTool, new Object[] { System.out, System.err, getJarToolArguments() });
      if (result != null && result.intValue() != 0)
        throw new ArchiverException("Could not create modular JAR file. The JDK jar tool exited with " + result); 
      if (!this.hasJarDateOption && getLastModifiedTime() != null) {
        getLogger().debug("Fix last modified time zip entries.");
        fixLastModifiedTimeZipEntries();
      } 
    } catch (IOException|ReflectiveOperationException|SecurityException e) {
      throw new ArchiverException("Exception occurred while creating modular JAR file", e);
    } 
  }
  
  private void fixLastModifiedTimeZipEntries() throws IOException {
    long timeMillis = getLastModifiedTime().toMillis();
    Path destFile = getDestFile().toPath();
    Path tmpZip = Files.createTempFile(destFile.getParent(), null, null, (FileAttribute<?>[])new FileAttribute[0]);
    ZipFile zipFile = new ZipFile(getDestFile());
    try {
      ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(tmpZip, new java.nio.file.OpenOption[0]));
      try {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          entry.setTime(timeMillis);
          out.putNextEntry(entry);
          if (!entry.isDirectory())
            IOUtil.copy(zipFile.getInputStream(entry), out); 
          out.closeEntry();
        } 
        out.close();
      } catch (Throwable throwable) {
        try {
          out.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      zipFile.close();
    } catch (Throwable throwable) {
      try {
        zipFile.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    Files.move(tmpZip, destFile, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
  }
  
  private boolean isModuleDescriptor(String path) {
    if (path.endsWith("module-info.class")) {
      String prefix = path.substring(0, path
          .lastIndexOf("module-info.class"));
      return (prefix.isEmpty() || MRJAR_VERSION_AREA
        .matcher(prefix).matches());
    } 
    return false;
  }
  
  private String[] getJarToolArguments() throws IOException {
    File tempEmptyDir = Files.createTempDirectory(null, (FileAttribute<?>[])new FileAttribute[0]).toFile();
    tempEmptyDir.deleteOnExit();
    List<String> args = new ArrayList<>();
    args.add("--update");
    args.add("--file");
    args.add(getDestFile().getAbsolutePath());
    String mainClass = (getModuleMainClass() != null) ? getModuleMainClass() : getManifestMainClass();
    if (mainClass != null) {
      args.add("--main-class");
      args.add(mainClass);
    } 
    if (getModuleVersion() != null) {
      args.add("--module-version");
      args.add(getModuleVersion());
    } 
    if (!isCompress())
      args.add("--no-compress"); 
    if (this.hasJarDateOption) {
      FileTime localTime = revertToLocalTime(getLastModifiedTime());
      args.add("--date");
      args.add(localTime.toString());
    } 
    args.add("-C");
    args.add(tempEmptyDir.getAbsolutePath());
    args.add(".");
    return args.<String>toArray(new String[0]);
  }
  
  private static FileTime revertToLocalTime(FileTime time) {
    long restoreToLocalTime = time.toMillis();
    Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.ROOT);
    cal.setTimeInMillis(restoreToLocalTime);
    restoreToLocalTime += (cal.get(15) + cal.get(16));
    return FileTime.fromMillis(restoreToLocalTime);
  }
  
  private boolean isJarDateOptionSupported(Method runMethod) {
    try {
      String[] args = { "--date", "2099-12-31T23:59:59Z", "--version" };
      NullPrintStream nullPrintStream = NullPrintStream.NULL_PRINT_STREAM;
      Integer result = (Integer)runMethod.invoke(this.jarTool, new Object[] { nullPrintStream, nullPrintStream, args });
      return (result != null && result.intValue() == 0);
    } catch (ReflectiveOperationException|SecurityException e) {
      return false;
    } 
  }
}
