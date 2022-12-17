package org.codehaus.plexus.archiver;

import javax.annotation.CheckForNull;
import org.codehaus.plexus.components.io.filemappers.FileMapper;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

public interface BaseFileSet {
  @CheckForNull
  String getPrefix();
  
  @CheckForNull
  String[] getIncludes();
  
  @CheckForNull
  String[] getExcludes();
  
  boolean isCaseSensitive();
  
  boolean isUsingDefaultExcludes();
  
  boolean isIncludingEmptyDirectories();
  
  @CheckForNull
  FileSelector[] getFileSelectors();
  
  InputStreamTransformer getStreamTransformer();
  
  @CheckForNull
  FileMapper[] getFileMappers();
}
