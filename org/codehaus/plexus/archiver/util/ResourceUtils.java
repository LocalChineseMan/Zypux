package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.codehaus.plexus.components.io.functions.FileSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.IOUtil;

public class ResourceUtils {
  public static boolean isUptodate(PlexusIoResource source, File destination) {
    return isUptodate(source, destination.lastModified());
  }
  
  public static boolean isUptodate(PlexusIoResource source, long destinationDate) {
    long s = source.getLastModified();
    if (s == 0L)
      return false; 
    if (destinationDate == 0L)
      return false; 
    return (destinationDate > s);
  }
  
  public static boolean isUptodate(long sourceDate, long destinationDate) {
    if (sourceDate == 0L)
      return false; 
    if (destinationDate == 0L)
      return false; 
    return (destinationDate > sourceDate);
  }
  
  public static void copyFile(PlexusIoResource in, File outFile) throws IOException {
    InputStream input = in.getContents();
    try {
      OutputStream output = Files.newOutputStream(outFile.toPath(), new java.nio.file.OpenOption[0]);
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
  
  public static void copyFile(InputStream input, File outFile) throws IOException {
    OutputStream output = null;
    try {
      output = Files.newOutputStream(outFile.toPath(), new java.nio.file.OpenOption[0]);
      IOUtil.copy(input, output);
      output.close();
      output = null;
      input.close();
      input = null;
    } finally {
      IOUtil.close(input);
      IOUtil.close(output);
    } 
  }
  
  public static boolean isSame(PlexusIoResource resource, File file) {
    if (resource instanceof FileSupplier) {
      File resourceFile = ((FileSupplier)resource).getFile();
      return file.equals(resourceFile);
    } 
    return false;
  }
  
  public static boolean isCanonicalizedSame(PlexusIoResource resource, File file) throws IOException {
    if (resource instanceof FileSupplier) {
      File resourceFile = ((FileSupplier)resource).getFile();
      return file.getCanonicalFile().equals(resourceFile.getCanonicalFile());
    } 
    return false;
  }
}
