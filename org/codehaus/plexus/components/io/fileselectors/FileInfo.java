package org.codehaus.plexus.components.io.fileselectors;

import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.components.io.functions.NameSupplier;

public interface FileInfo extends NameSupplier {
  String getName();
  
  InputStream getContents() throws IOException;
  
  boolean isFile();
  
  boolean isDirectory();
  
  boolean isSymbolicLink();
}
