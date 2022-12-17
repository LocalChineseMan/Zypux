package org.codehaus.plexus.archiver.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.IOUtil;

public class Streams {
  public static final InputStream EMPTY_INPUTSTREAM = new ByteArrayInputStream(new byte[0]);
  
  public static BufferedInputStream bufferedInputStream(InputStream is) {
    return (is instanceof BufferedInputStream) ? 
      (BufferedInputStream)is : 
      new BufferedInputStream(is, 65536);
  }
  
  public static BufferedOutputStream bufferedOutputStream(OutputStream os) {
    return (os instanceof BufferedOutputStream) ? 
      (BufferedOutputStream)os : 
      new BufferedOutputStream(os, 65536);
  }
  
  public static byte[] cacheBuffer() {
    return new byte[8192];
  }
  
  public static InputStream fileInputStream(File file) throws IOException {
    return Files.newInputStream(file.toPath(), new java.nio.file.OpenOption[0]);
  }
  
  public static InputStream fileInputStream(File file, String operation) throws ArchiverException {
    try {
      return Files.newInputStream(file.toPath(), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      throw new ArchiverException("Problem reading input file for " + operation + " " + file
          .getParent() + ", " + e.getMessage());
    } 
  }
  
  public static OutputStream fileOutputStream(File file) throws IOException {
    return Files.newOutputStream(file.toPath(), new java.nio.file.OpenOption[0]);
  }
  
  public static OutputStream fileOutputStream(File file, String operation) throws ArchiverException {
    try {
      return Files.newOutputStream(file.toPath(), new java.nio.file.OpenOption[0]);
    } catch (IOException e) {
      throw new ArchiverException("Problem creating output file for " + operation + " " + file
          .getParent() + ", " + e.getMessage());
    } 
  }
  
  public static void copyFully(@WillClose InputStream zIn, @WillClose OutputStream out, String gzip) throws ArchiverException {
    try {
      copyFullyDontCloseOutput(zIn, out, gzip);
      out.close();
      out = null;
    } catch (IOException e) {
      throw new ArchiverException("Failure copying.", e);
    } finally {
      IOUtil.close(out);
    } 
  }
  
  public static void copyFullyDontCloseOutput(@WillClose InputStream zIn, @WillNotClose OutputStream out, String gzip) throws ArchiverException {
    try {
      byte[] buffer = cacheBuffer();
      int count = 0;
      while (true) {
        try {
          out.write(buffer, 0, count);
        } catch (IOException e) {
          throw new ArchiverException("Problem writing to output in " + gzip + " operation " + e
              .getMessage());
        } 
        count = zIn.read(buffer, 0, buffer.length);
        if (count == -1) {
          zIn.close();
          zIn = null;
          return;
        } 
      } 
    } catch (IOException e) {
      throw new ArchiverException("Problem reading from source file in " + gzip + " operation " + e
          .getMessage());
    } finally {
      IOUtil.close(zIn);
    } 
  }
}
