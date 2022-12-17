package org.codehaus.plexus.archiver;

import java.io.InputStream;

@Deprecated
public interface ArchiveFileFilter {
  boolean include(InputStream paramInputStream, String paramString);
}
