package org.codehaus.plexus.util;

import java.io.File;
import java.util.Comparator;

public interface Scanner {
  void setIncludes(java.lang.String[] paramArrayOfString);
  
  void setExcludes(java.lang.String[] paramArrayOfString);
  
  void addDefaultExcludes();
  
  void scan();
  
  java.lang.String[] getIncludedFiles();
  
  java.lang.String[] getIncludedDirectories();
  
  File getBasedir();
  
  void setFilenameComparator(Comparator<java.lang.String> paramComparator);
}
