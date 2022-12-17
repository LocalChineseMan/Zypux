package org.codehaus.plexus.archiver;

import java.io.File;
import javax.annotation.CheckForNull;

public interface ArchivedFileSet extends BaseFileSet {
  @CheckForNull
  File getArchive();
}
