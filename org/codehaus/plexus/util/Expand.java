package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Expand {
  private File dest;
  
  private File source;
  
  private boolean overwrite = true;
  
  public void execute() throws Exception {
    expandFile(this.source, this.dest);
  }
  
  protected void expandFile(File srcF, File dir) throws Exception {
    try {
      ZipInputStream zis = new ZipInputStream(Files.newInputStream(srcF.toPath(), new java.nio.file.OpenOption[0]));
      try {
        for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry())
          extractFile(srcF, dir, zis, ze.getName(), new Date(ze.getTime()), ze.isDirectory()); 
        zis.close();
      } catch (Throwable throwable) {
        try {
          zis.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException ioe) {
      throw new Exception("Error while expanding " + srcF.getPath(), ioe);
    } 
  }
  
  protected void extractFile(File srcF, File dir, InputStream compressedInputStream, java.lang.String entryName, Date entryDate, boolean isDirectory) throws Exception {
    File f = FileUtils.resolveFile(dir, entryName);
    if (!f.getAbsolutePath().startsWith(dir.getAbsolutePath()))
      throw new IOException("Entry '" + entryName + "' outside the target directory."); 
    try {
      if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime())
        return; 
      File dirF = f.getParentFile();
      dirF.mkdirs();
      if (isDirectory) {
        f.mkdirs();
      } else {
        byte[] buffer = new byte[65536];
        OutputStream fos = Files.newOutputStream(f.toPath(), new java.nio.file.OpenOption[0]);
        try {
          int length = compressedInputStream.read(buffer);
          while (length >= 0) {
            fos.write(buffer, 0, length);
            length = compressedInputStream.read(buffer);
          } 
          if (fos != null)
            fos.close(); 
        } catch (Throwable throwable) {
          if (fos != null)
            try {
              fos.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } 
      f.setLastModified(entryDate.getTime());
    } catch (FileNotFoundException ex) {
      throw new Exception("Can't extract file " + srcF.getPath(), ex);
    } 
  }
  
  public void setDest(File d) {
    this.dest = d;
  }
  
  public void setSrc(File s) {
    this.source = s;
  }
  
  public void setOverwrite(boolean b) {
    this.overwrite = b;
  }
}
