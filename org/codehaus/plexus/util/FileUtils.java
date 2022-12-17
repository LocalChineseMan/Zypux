package org.codehaus.plexus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.codehaus.plexus.util.io.InputStreamFacade;
import org.codehaus.plexus.util.io.URLInputStreamFacade;

public class FileUtils {
  public static final int ONE_KB = 1024;
  
  public static final int ONE_MB = 1048576;
  
  public static final int ONE_GB = 1073741824;
  
  public static java.lang.String FS = File.separator;
  
  private static final java.lang.String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new java.lang.String[] { ":", "*", "?", "\"", "<", ">", "|" };
  
  public static java.lang.String[] getDefaultExcludes() {
    return DirectoryScanner.DEFAULTEXCLUDES;
  }
  
  public static List<java.lang.String> getDefaultExcludesAsList() {
    return Arrays.asList(getDefaultExcludes());
  }
  
  public static java.lang.String getDefaultExcludesAsString() {
    return StringUtils.join((Object[])DirectoryScanner.DEFAULTEXCLUDES, ",");
  }
  
  public static java.lang.String byteCountToDisplaySize(int size) {
    java.lang.String displaySize;
    if (size / 1073741824 > 0) {
      displaySize = java.lang.String.valueOf(size / 1073741824) + " GB";
    } else if (size / 1048576 > 0) {
      displaySize = java.lang.String.valueOf(size / 1048576) + " MB";
    } else if (size / 1024 > 0) {
      displaySize = java.lang.String.valueOf(size / 1024) + " KB";
    } else {
      displaySize = java.lang.String.valueOf(size) + " bytes";
    } 
    return displaySize;
  }
  
  public static java.lang.String dirname(java.lang.String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i >= 0) ? filename.substring(0, i) : "";
  }
  
  public static java.lang.String filename(java.lang.String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i >= 0) ? filename.substring(i + 1) : filename;
  }
  
  public static java.lang.String basename(java.lang.String filename) {
    return basename(filename, extension(filename));
  }
  
  public static java.lang.String basename(java.lang.String filename, java.lang.String suffix) {
    int i = filename.lastIndexOf(File.separator) + 1;
    int lastDot = (suffix != null && suffix.length() > 0) ? filename.lastIndexOf(suffix) : -1;
    if (lastDot >= 0)
      return filename.substring(i, lastDot); 
    if (i > 0)
      return filename.substring(i); 
    return filename;
  }
  
  public static java.lang.String extension(java.lang.String filename) {
    int lastDot, lastSep = filename.lastIndexOf(File.separatorChar);
    if (lastSep < 0) {
      lastDot = filename.lastIndexOf('.');
    } else {
      lastDot = filename.substring(lastSep + 1).lastIndexOf('.');
      if (lastDot >= 0)
        lastDot += lastSep + 1; 
    } 
    if (lastDot >= 0 && lastDot > lastSep)
      return filename.substring(lastDot + 1); 
    return "";
  }
  
  public static boolean fileExists(java.lang.String fileName) {
    File file = new File(fileName);
    return file.exists();
  }
  
  public static java.lang.String fileRead(java.lang.String file) throws IOException {
    return fileRead(file, (java.lang.String)null);
  }
  
  public static java.lang.String fileRead(java.lang.String file, java.lang.String encoding) throws IOException {
    return fileRead(new File(file), encoding);
  }
  
  public static java.lang.String fileRead(File file) throws IOException {
    return fileRead(file, (java.lang.String)null);
  }
  
  public static java.lang.String fileRead(File file, java.lang.String encoding) throws IOException {
    StringBuilder buf = new StringBuilder();
    Reader reader = getInputStreamReader(file, encoding);
    try {
      char[] b = new char[512];
      int count;
      while ((count = reader.read(b)) >= 0)
        buf.append(b, 0, count); 
      if (reader != null)
        reader.close(); 
    } catch (Throwable throwable) {
      if (reader != null)
        try {
          reader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return buf.toString();
  }
  
  private static InputStreamReader getInputStreamReader(File file, java.lang.String encoding) throws IOException {
    if (encoding != null)
      return new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]), encoding); 
    return new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]));
  }
  
  public static void fileAppend(java.lang.String fileName, java.lang.String data) throws IOException {
    fileAppend(fileName, null, data);
  }
  
  public static void fileAppend(java.lang.String fileName, java.lang.String encoding, java.lang.String data) throws IOException {
    OutputStream out = Files.newOutputStream(Paths.get(fileName, new java.lang.String[0]), new OpenOption[] { StandardOpenOption.APPEND, StandardOpenOption.CREATE });
    try {
      if (encoding != null) {
        out.write(data.getBytes(encoding));
      } else {
        out.write(data.getBytes());
      } 
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
  
  public static void fileWrite(java.lang.String fileName, java.lang.String data) throws IOException {
    fileWrite(fileName, (java.lang.String)null, data);
  }
  
  public static void fileWrite(java.lang.String fileName, java.lang.String encoding, java.lang.String data) throws IOException {
    File file = (fileName == null) ? null : new File(fileName);
    fileWrite(file, encoding, data);
  }
  
  public static void fileWrite(File file, java.lang.String data) throws IOException {
    fileWrite(file, (java.lang.String)null, data);
  }
  
  public static void fileWrite(File file, java.lang.String encoding, java.lang.String data) throws IOException {
    Writer writer = getOutputStreamWriter(file, encoding);
    try {
      writer.write(data);
      if (writer != null)
        writer.close(); 
    } catch (Throwable throwable) {
      if (writer != null)
        try {
          writer.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  private static OutputStreamWriter getOutputStreamWriter(File file, java.lang.String encoding) throws IOException {
    OutputStream out = Files.newOutputStream(file.toPath(), new OpenOption[0]);
    if (encoding != null)
      return new OutputStreamWriter(out, encoding); 
    return new OutputStreamWriter(out);
  }
  
  public static void fileDelete(java.lang.String fileName) {
    File file = new File(fileName);
    try {
      NioFiles.deleteIfExists(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static boolean waitFor(java.lang.String fileName, int seconds) {
    return waitFor(new File(fileName), seconds);
  }
  
  public static boolean waitFor(File file, int seconds) {
    int timeout = 0;
    int tick = 0;
    while (!file.exists()) {
      if (tick++ >= 10) {
        tick = 0;
        if (timeout++ > seconds)
          return false; 
      } 
      try {
        Thread.sleep(100L);
      } catch (InterruptedException interruptedException) {}
    } 
    return true;
  }
  
  public static File getFile(java.lang.String fileName) {
    return new File(fileName);
  }
  
  public static java.lang.String[] getFilesFromExtension(java.lang.String directory, java.lang.String[] extensions) {
    List<java.lang.String> files = new ArrayList<>();
    File currentDir = new File(directory);
    java.lang.String[] unknownFiles = currentDir.list();
    if (unknownFiles == null)
      return new java.lang.String[0]; 
    for (java.lang.String unknownFile : unknownFiles) {
      java.lang.String currentFileName = directory + System.getProperty("file.separator") + unknownFile;
      File currentFile = new File(currentFileName);
      if (currentFile.isDirectory()) {
        if (!currentFile.getName().equals("CVS")) {
          java.lang.String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
          files = blendFilesToVector(files, fetchFiles);
        } 
      } else {
        java.lang.String add = currentFile.getAbsolutePath();
        if (isValidFile(add, extensions))
          files.add(add); 
      } 
    } 
    return files.<java.lang.String>toArray(new java.lang.String[0]);
  }
  
  private static List<java.lang.String> blendFilesToVector(List<java.lang.String> v, java.lang.String[] files) {
    for (java.lang.String file : files)
      v.add(file); 
    return v;
  }
  
  private static boolean isValidFile(java.lang.String file, java.lang.String[] extensions) {
    java.lang.String extension = extension(file);
    if (extension == null)
      extension = ""; 
    for (java.lang.String extension1 : extensions) {
      if (extension1.equals(extension))
        return true; 
    } 
    return false;
  }
  
  public static void mkdir(java.lang.String dir) {
    File file = new File(dir);
    if (Os.isFamily("windows") && !isValidWindowsFileName(file))
      throw new IllegalArgumentException("The file (" + dir + ") cannot contain any of the following characters: \n" + 
          
          StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " ")); 
    if (!file.exists())
      file.mkdirs(); 
  }
  
  public static boolean contentEquals(File file1, File file2) throws IOException {
    boolean file1Exists = file1.exists();
    if (file1Exists != file2.exists())
      return false; 
    if (!file1Exists)
      return true; 
    if (file1.isDirectory() || file2.isDirectory())
      return false; 
    InputStream input1 = Files.newInputStream(file1.toPath(), new OpenOption[0]);
    try {
      InputStream input2 = Files.newInputStream(file2.toPath(), new OpenOption[0]);
      try {
        boolean bool = IOUtil.contentEquals(input1, input2);
        if (input2 != null)
          input2.close(); 
        if (input1 != null)
          input1.close(); 
        return bool;
      } catch (Throwable throwable) {
        if (input2 != null)
          try {
            input2.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (Throwable throwable) {
      if (input1 != null)
        try {
          input1.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static File toFile(URL url) {
    if (url == null || !url.getProtocol().equalsIgnoreCase("file"))
      return null; 
    java.lang.String filename = url.getFile().replace('/', File.separatorChar);
    int pos = -1;
    while ((pos = filename.indexOf('%', pos + 1)) >= 0) {
      if (pos + 2 < filename.length()) {
        java.lang.String hexStr = filename.substring(pos + 1, pos + 3);
        char ch = (char)Integer.parseInt(hexStr, 16);
        filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
      } 
    } 
    return new File(filename);
  }
  
  public static URL[] toURLs(File[] files) throws IOException {
    URL[] urls = new URL[files.length];
    for (int i = 0; i < urls.length; i++)
      urls[i] = files[i].toURI().toURL(); 
    return urls;
  }
  
  public static java.lang.String removeExtension(java.lang.String filename) {
    java.lang.String ext = extension(filename);
    if ("".equals(ext))
      return filename; 
    int index = filename.lastIndexOf(ext) - 1;
    return filename.substring(0, index);
  }
  
  public static java.lang.String getExtension(java.lang.String filename) {
    return extension(filename);
  }
  
  public static java.lang.String removePath(java.lang.String filepath) {
    return removePath(filepath, File.separatorChar);
  }
  
  public static java.lang.String removePath(java.lang.String filepath, char fileSeparatorChar) {
    int index = filepath.lastIndexOf(fileSeparatorChar);
    if (-1 == index)
      return filepath; 
    return filepath.substring(index + 1);
  }
  
  public static java.lang.String getPath(java.lang.String filepath) {
    return getPath(filepath, File.separatorChar);
  }
  
  public static java.lang.String getPath(java.lang.String filepath, char fileSeparatorChar) {
    int index = filepath.lastIndexOf(fileSeparatorChar);
    if (-1 == index)
      return ""; 
    return filepath.substring(0, index);
  }
  
  public static void copyFileToDirectory(java.lang.String source, java.lang.String destinationDirectory) throws IOException {
    copyFileToDirectory(new File(source), new File(destinationDirectory));
  }
  
  public static void copyFileToDirectoryIfModified(java.lang.String source, java.lang.String destinationDirectory) throws IOException {
    copyFileToDirectoryIfModified(new File(source), new File(destinationDirectory));
  }
  
  public static void copyFileToDirectory(File source, File destinationDirectory) throws IOException {
    if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
      throw new IllegalArgumentException("Destination is not a directory"); 
    copyFile(source, new File(destinationDirectory, source.getName()));
  }
  
  public static void copyFileToDirectoryIfModified(File source, File destinationDirectory) throws IOException {
    if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
      throw new IllegalArgumentException("Destination is not a directory"); 
    copyFileIfModified(source, new File(destinationDirectory, source.getName()));
  }
  
  public static void mkDirs(File sourceBase, java.lang.String[] dirs, File destination) throws IOException {
    for (java.lang.String dir : dirs) {
      File src = new File(sourceBase, dir);
      File dst = new File(destination, dir);
      if (NioFiles.isSymbolicLink(src)) {
        File target = NioFiles.readSymbolicLink(src);
        NioFiles.createSymbolicLink(dst, target);
      } else {
        dst.mkdirs();
      } 
    } 
  }
  
  public static void copyFile(File source, File destination) throws IOException {
    if (!source.exists()) {
      java.lang.String message = "File " + source + " does not exist";
      throw new IOException(message);
    } 
    if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
      return; 
    mkdirsFor(destination);
    doCopyFile(source, destination);
    if (source.length() != destination.length()) {
      java.lang.String message = "Failed to copy full contents from " + source + " to " + destination;
      throw new IOException(message);
    } 
  }
  
  private static void doCopyFile(File source, File destination) throws IOException {
    doCopyFileUsingNewIO(source, destination);
  }
  
  private static void doCopyFileUsingNewIO(File source, File destination) throws IOException {
    NioFiles.copy(source, destination);
  }
  
  public static void linkFile(File source, File destination) throws IOException {
    if (!source.exists()) {
      java.lang.String message = "File " + source + " does not exist";
      throw new IOException(message);
    } 
    if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
      return; 
    mkdirsFor(destination);
    NioFiles.createSymbolicLink(destination, source);
  }
  
  public static boolean copyFileIfModified(File source, File destination) throws IOException {
    if (isSourceNewerThanDestination(source, destination)) {
      copyFile(source, destination);
      return true;
    } 
    return false;
  }
  
  public static void copyURLToFile(URL source, File destination) throws IOException {
    copyStreamToFile((InputStreamFacade)new URLInputStreamFacade(source), destination);
  }
  
  public static void copyStreamToFile(InputStreamFacade source, File destination) throws IOException {
    mkdirsFor(destination);
    checkCanWrite(destination);
    InputStream input = source.getInputStream();
    try {
      OutputStream output = Files.newOutputStream(destination.toPath(), new OpenOption[0]);
      try {
        IOUtil.copy(input, output);
        if (output != null)
          output.close(); 
      } catch (Throwable throwable) {
        if (output != null)
          try {
            output.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      if (input != null)
        input.close(); 
    } catch (Throwable throwable) {
      if (input != null)
        try {
          input.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  private static void checkCanWrite(File destination) throws IOException {
    if (destination.exists() && !destination.canWrite()) {
      java.lang.String message = "Unable to open file " + destination + " for writing.";
      throw new IOException(message);
    } 
  }
  
  private static void mkdirsFor(File destination) {
    File parentFile = destination.getParentFile();
    if (parentFile != null && !parentFile.exists())
      parentFile.mkdirs(); 
  }
  
  public static java.lang.String normalize(java.lang.String path) {
    java.lang.String normalized = path;
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0)
        break; 
      normalized = normalized.substring(0, index) + normalized.substring(index + 1);
    } 
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0)
        break; 
      normalized = normalized.substring(0, index) + normalized.substring(index + 2);
    } 
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0)
        break; 
      if (index == 0)
        return null; 
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
    } 
    return normalized;
  }
  
  public static java.lang.String catPath(java.lang.String lookupPath, java.lang.String path) {
    int index = lookupPath.lastIndexOf("/");
    java.lang.String lookup = lookupPath.substring(0, index);
    java.lang.String pth = path;
    while (pth.startsWith("../")) {
      if (lookup.length() > 0) {
        index = lookup.lastIndexOf("/");
        lookup = lookup.substring(0, index);
      } else {
        return null;
      } 
      index = pth.indexOf("../") + 3;
      pth = pth.substring(index);
    } 
    return lookup + "/" + pth;
  }
  
  public static File resolveFile(File baseFile, java.lang.String filename) {
    java.lang.String filenm = filename;
    if ('/' != File.separatorChar)
      filenm = filename.replace('/', File.separatorChar); 
    if ('\\' != File.separatorChar)
      filenm = filename.replace('\\', File.separatorChar); 
    if (filenm.startsWith(File.separator) || (Os.isFamily("windows") && filenm.indexOf(":") > 0)) {
      File file1 = new File(filenm);
      try {
        file1 = file1.getCanonicalFile();
      } catch (IOException iOException) {}
      return file1;
    } 
    char[] chars = filename.toCharArray();
    StringBuilder sb = new StringBuilder();
    int start = 0;
    if ('\\' == File.separatorChar) {
      sb.append(filenm.charAt(0));
      start++;
    } 
    for (int i = start; i < chars.length; i++) {
      boolean doubleSeparator = (File.separatorChar == chars[i] && File.separatorChar == chars[i - 1]);
      if (!doubleSeparator)
        sb.append(chars[i]); 
    } 
    filenm = sb.toString();
    File file = (new File(baseFile, filenm)).getAbsoluteFile();
    try {
      file = file.getCanonicalFile();
    } catch (IOException iOException) {}
    return file;
  }
  
  public static void forceDelete(java.lang.String file) throws IOException {
    forceDelete(new File(file));
  }
  
  public static void forceDelete(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectory(file);
    } else {
      boolean filePresent = file.getCanonicalFile().exists();
      if (!deleteFile(file) && filePresent) {
        java.lang.String message = "File " + file + " unable to be deleted.";
        throw new IOException(message);
      } 
    } 
  }
  
  private static boolean deleteFile(File file) throws IOException {
    if (file.isDirectory())
      throw new IOException("File " + file + " isn't a file."); 
    if (!file.delete()) {
      if (Os.isFamily("windows")) {
        file = file.getCanonicalFile();
        System.gc();
      } 
      try {
        Thread.sleep(10L);
        return file.delete();
      } catch (InterruptedException ignore) {
        return file.delete();
      } 
    } 
    return true;
  }
  
  public static void forceDeleteOnExit(File file) throws IOException {
    if (!file.exists())
      return; 
    if (file.isDirectory()) {
      deleteDirectoryOnExit(file);
    } else {
      file.deleteOnExit();
    } 
  }
  
  private static void deleteDirectoryOnExit(File directory) throws IOException {
    if (!directory.exists())
      return; 
    directory.deleteOnExit();
    cleanDirectoryOnExit(directory);
  }
  
  private static void cleanDirectoryOnExit(File directory) throws IOException {
    if (!directory.exists()) {
      java.lang.String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      java.lang.String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    IOException exception = null;
    File[] files = directory.listFiles();
    for (File file : files) {
      try {
        forceDeleteOnExit(file);
      } catch (IOException ioe) {
        exception = ioe;
      } 
    } 
    if (null != exception)
      throw exception; 
  }
  
  public static void forceMkdir(File file) throws IOException {
    if (Os.isFamily("windows"))
      if (!isValidWindowsFileName(file))
        throw new IllegalArgumentException("The file (" + file.getAbsolutePath() + ") cannot contain any of the following characters: \n" + 
            
            StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));  
    if (file.exists()) {
      if (file.isFile()) {
        java.lang.String message = "File " + file + " exists and is not a directory. Unable to create directory.";
        throw new IOException(message);
      } 
    } else if (false == file.mkdirs()) {
      java.lang.String message = "Unable to create directory " + file;
      throw new IOException(message);
    } 
  }
  
  public static void deleteDirectory(java.lang.String directory) throws IOException {
    deleteDirectory(new File(directory));
  }
  
  public static void deleteDirectory(File directory) throws IOException {
    if (!directory.exists())
      return; 
    if (directory.delete())
      return; 
    cleanDirectory(directory);
    if (!directory.delete()) {
      java.lang.String message = "Directory " + directory + " unable to be deleted.";
      throw new IOException(message);
    } 
  }
  
  public static void cleanDirectory(java.lang.String directory) throws IOException {
    cleanDirectory(new File(directory));
  }
  
  public static void cleanDirectory(File directory) throws IOException {
    if (!directory.exists()) {
      java.lang.String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      java.lang.String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    IOException exception = null;
    File[] files = directory.listFiles();
    if (files == null)
      return; 
    for (File file : files) {
      try {
        forceDelete(file);
      } catch (IOException ioe) {
        exception = ioe;
      } 
    } 
    if (null != exception)
      throw exception; 
  }
  
  public static long sizeOfDirectory(java.lang.String directory) {
    return sizeOfDirectory(new File(directory));
  }
  
  public static long sizeOfDirectory(File directory) {
    if (!directory.exists()) {
      java.lang.String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      java.lang.String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    long size = 0L;
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        size += sizeOfDirectory(file);
      } else {
        size += file.length();
      } 
    } 
    return size;
  }
  
  public static List<File> getFiles(File directory, java.lang.String includes, java.lang.String excludes) throws IOException {
    return getFiles(directory, includes, excludes, true);
  }
  
  public static List<File> getFiles(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir) throws IOException {
    List<java.lang.String> fileNames = getFileNames(directory, includes, excludes, includeBasedir);
    List<File> files = new ArrayList<>();
    for (java.lang.String filename : fileNames)
      files.add(new File(filename)); 
    return files;
  }
  
  public static List<java.lang.String> getFileNames(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir) throws IOException {
    return getFileNames(directory, includes, excludes, includeBasedir, true);
  }
  
  public static List<java.lang.String> getFileNames(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
    return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, true, false);
  }
  
  public static List<java.lang.String> getDirectoryNames(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir) throws IOException {
    return getDirectoryNames(directory, includes, excludes, includeBasedir, true);
  }
  
  public static List<java.lang.String> getDirectoryNames(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
    return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, false, true);
  }
  
  public static List<java.lang.String> getFileAndDirectoryNames(File directory, java.lang.String includes, java.lang.String excludes, boolean includeBasedir, boolean isCaseSensitive, boolean getFiles, boolean getDirectories) throws IOException {
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(directory);
    if (includes != null)
      scanner.setIncludes(StringUtils.split(includes, ",")); 
    if (excludes != null)
      scanner.setExcludes(StringUtils.split(excludes, ",")); 
    scanner.setCaseSensitive(isCaseSensitive);
    scanner.scan();
    List<java.lang.String> list = new ArrayList<>();
    if (getFiles) {
      java.lang.String[] files = scanner.getIncludedFiles();
      for (java.lang.String file : files) {
        if (includeBasedir) {
          list.add(directory + FS + file);
        } else {
          list.add(file);
        } 
      } 
    } 
    if (getDirectories) {
      java.lang.String[] directories = scanner.getIncludedDirectories();
      for (java.lang.String directory1 : directories) {
        if (includeBasedir) {
          list.add(directory + FS + directory1);
        } else {
          list.add(directory1);
        } 
      } 
    } 
    return list;
  }
  
  public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectory(sourceDirectory, destinationDirectory, "**", null);
  }
  
  public static void copyDirectory(File sourceDirectory, File destinationDirectory, java.lang.String includes, java.lang.String excludes) throws IOException {
    if (!sourceDirectory.exists())
      return; 
    List<File> files = getFiles(sourceDirectory, includes, excludes);
    for (File file : files)
      copyFileToDirectory(file, destinationDirectory); 
  }
  
  public static void copyDirectoryLayout(File sourceDirectory, File destinationDirectory, java.lang.String[] includes, java.lang.String[] excludes) throws IOException {
    if (sourceDirectory == null)
      throw new IOException("source directory can't be null."); 
    if (destinationDirectory == null)
      throw new IOException("destination directory can't be null."); 
    if (sourceDirectory.equals(destinationDirectory))
      throw new IOException("source and destination are the same directory."); 
    if (!sourceDirectory.exists())
      throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")."); 
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(sourceDirectory);
    if (includes != null && includes.length >= 1) {
      scanner.setIncludes(includes);
    } else {
      scanner.setIncludes(new java.lang.String[] { "**" });
    } 
    if (excludes != null && excludes.length >= 1)
      scanner.setExcludes(excludes); 
    scanner.addDefaultExcludes();
    scanner.scan();
    List<java.lang.String> includedDirectories = Arrays.asList(scanner.getIncludedDirectories());
    for (java.lang.String name : includedDirectories) {
      File source = new File(sourceDirectory, name);
      if (source.equals(sourceDirectory))
        continue; 
      File destination = new File(destinationDirectory, name);
      destination.mkdirs();
    } 
  }
  
  public static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, false);
  }
  
  public static void copyDirectoryStructureIfModified(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, true);
  }
  
  private static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory, File rootDestinationDirectory, boolean onlyModifiedFiles) throws IOException {
    if (sourceDirectory == null)
      throw new IOException("source directory can't be null."); 
    if (destinationDirectory == null)
      throw new IOException("destination directory can't be null."); 
    if (sourceDirectory.equals(destinationDirectory))
      throw new IOException("source and destination are the same directory."); 
    if (!sourceDirectory.exists())
      throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")."); 
    File[] files = sourceDirectory.listFiles();
    java.lang.String sourcePath = sourceDirectory.getAbsolutePath();
    for (File file : files) {
      if (!file.equals(rootDestinationDirectory)) {
        java.lang.String dest = file.getAbsolutePath();
        dest = dest.substring(sourcePath.length() + 1);
        File destination = new File(destinationDirectory, dest);
        if (file.isFile()) {
          destination = destination.getParentFile();
          if (onlyModifiedFiles) {
            copyFileToDirectoryIfModified(file, destination);
          } else {
            copyFileToDirectory(file, destination);
          } 
        } else if (file.isDirectory()) {
          if (!destination.exists() && !destination.mkdirs())
            throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'."); 
          copyDirectoryStructure(file, destination, rootDestinationDirectory, onlyModifiedFiles);
        } else {
          throw new IOException("Unknown file type: " + file.getAbsolutePath());
        } 
      } 
    } 
  }
  
  public static void rename(File from, File to) throws IOException {
    if (to.exists() && !to.delete())
      throw new IOException("Failed to delete " + to + " while trying to rename " + from); 
    File parent = to.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs())
      throw new IOException("Failed to create directory " + parent + " while trying to rename " + from); 
    if (!from.renameTo(to)) {
      copyFile(from, to);
      if (!from.delete())
        throw new IOException("Failed to delete " + from + " while trying to rename it."); 
    } 
  }
  
  public static File createTempFile(java.lang.String prefix, java.lang.String suffix, File parentDir) {
    File result = null;
    java.lang.String parent = System.getProperty("java.io.tmpdir");
    if (parentDir != null)
      parent = parentDir.getPath(); 
    DecimalFormat fmt = new DecimalFormat("#####");
    SecureRandom secureRandom = new SecureRandom();
    long secureInitializer = secureRandom.nextLong();
    Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
    synchronized (rand) {
      while (true) {
        result = new File(parent, prefix + fmt.format(Math.abs(rand.nextInt())) + suffix);
        if (!result.exists())
          return result; 
      } 
    } 
  }
  
  public static void copyFile(File from, File to, java.lang.String encoding, FilterWrapper[] wrappers) throws IOException {
    copyFile(from, to, encoding, wrappers, false);
  }
  
  public static abstract class FilterWrapper {
    public abstract Reader getReader(Reader param1Reader);
  }
  
  public static void copyFile(File from, File to, java.lang.String encoding, FilterWrapper[] wrappers, boolean overwrite) throws IOException {
    if (wrappers != null && wrappers.length > 0) {
      Reader fileReader = null;
      Writer fileWriter = null;
      try {
        if (encoding == null || encoding.length() < 1) {
          fileReader = Files.newBufferedReader(from.toPath());
          fileWriter = Files.newBufferedWriter(to.toPath(), new OpenOption[0]);
        } else {
          OutputStream outstream = Files.newOutputStream(to.toPath(), new OpenOption[0]);
          fileReader = Files.newBufferedReader(from.toPath(), Charset.forName(encoding));
          fileWriter = new OutputStreamWriter(outstream, encoding);
        } 
        Reader reader = fileReader;
        for (FilterWrapper wrapper : wrappers)
          reader = wrapper.getReader(reader); 
        IOUtil.copy(reader, fileWriter);
        fileWriter.close();
        fileWriter = null;
        fileReader.close();
        fileReader = null;
      } finally {
        IOUtil.close(fileReader);
        IOUtil.close(fileWriter);
      } 
    } else if (isSourceNewerThanDestination(from, to) || overwrite) {
      copyFile(from, to);
    } 
  }
  
  private static boolean isSourceNewerThanDestination(File source, File destination) {
    return ((destination.lastModified() == 0L && source.lastModified() == 0L) || destination.lastModified() < source.lastModified());
  }
  
  public static List<java.lang.String> loadFile(File file) throws IOException {
    List<java.lang.String> lines = new ArrayList<>();
    if (file.exists()) {
      BufferedReader reader = Files.newBufferedReader(file.toPath());
      try {
        for (java.lang.String line = reader.readLine(); line != null; line = reader.readLine()) {
          line = line.trim();
          if (!line.startsWith("#") && line.length() != 0)
            lines.add(line); 
        } 
        if (reader != null)
          reader.close(); 
      } catch (Throwable throwable) {
        if (reader != null)
          try {
            reader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    return lines;
  }
  
  public static boolean isValidWindowsFileName(File f) {
    if (Os.isFamily("windows")) {
      if (StringUtils.indexOfAny(f.getName(), INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME) != -1)
        return false; 
      File parentFile = f.getParentFile();
      if (parentFile != null)
        return isValidWindowsFileName(parentFile); 
    } 
    return true;
  }
}
