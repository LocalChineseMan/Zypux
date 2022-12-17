package org.codehaus.plexus.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.ArchiveEntry;

public interface ArchiveFile {
  Enumeration<? extends ArchiveEntry> getEntries() throws IOException;
  
  InputStream getInputStream(ArchiveEntry paramArchiveEntry) throws IOException;
}
