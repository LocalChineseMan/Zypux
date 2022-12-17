package org.codehaus.plexus.archiver;

import java.io.File;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

public interface UnArchiver {
  void extract() throws ArchiverException;
  
  void extract(String paramString, File paramFile) throws ArchiverException;
  
  File getDestDirectory();
  
  void setDestDirectory(File paramFile);
  
  File getDestFile();
  
  void setDestFile(File paramFile);
  
  File getSourceFile();
  
  void setSourceFile(File paramFile);
  
  boolean isOverwrite();
  
  void setOverwrite(boolean paramBoolean);
  
  FileMapper[] getFileMappers();
  
  void setFileMappers(FileMapper[] paramArrayOfFileMapper);
  
  void setFileSelectors(FileSelector[] paramArrayOfFileSelector);
  
  FileSelector[] getFileSelectors();
  
  @Deprecated
  void setUseJvmChmod(boolean paramBoolean);
  
  @Deprecated
  boolean isUseJvmChmod();
  
  boolean isIgnorePermissions();
  
  void setIgnorePermissions(boolean paramBoolean);
}
