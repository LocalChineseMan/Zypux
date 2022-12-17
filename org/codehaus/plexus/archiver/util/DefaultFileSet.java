package org.codehaus.plexus.archiver.util;

import java.io.File;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.FileSet;

public class DefaultFileSet extends AbstractFileSet<DefaultFileSet> implements FileSet {
  private File directory;
  
  public DefaultFileSet(File directory) {
    this.directory = directory;
  }
  
  public DefaultFileSet() {}
  
  public void setDirectory(@Nonnull File directory) {
    this.directory = directory;
  }
  
  @Nonnull
  public File getDirectory() {
    return this.directory;
  }
  
  public static DefaultFileSet fileSet(File directory) {
    DefaultFileSet defaultFileSet = new DefaultFileSet(directory);
    return defaultFileSet;
  }
}
