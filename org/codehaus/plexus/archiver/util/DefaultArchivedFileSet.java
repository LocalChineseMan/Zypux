package org.codehaus.plexus.archiver.util;

import java.io.File;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.ArchivedFileSet;

public class DefaultArchivedFileSet extends AbstractFileSet<DefaultArchivedFileSet> implements ArchivedFileSet {
  private final File archive;
  
  public DefaultArchivedFileSet(@Nonnull File archive) {
    this.archive = archive;
  }
  
  public File getArchive() {
    return this.archive;
  }
  
  public static DefaultArchivedFileSet archivedFileSet(File archiveFile) {
    if (archiveFile == null)
      throw new IllegalArgumentException("Archive File cannot be null"); 
    return new DefaultArchivedFileSet(archiveFile);
  }
}
