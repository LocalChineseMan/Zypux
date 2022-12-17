package org.codehaus.plexus.archiver;

import java.util.List;

public interface ArchiveFinalizer {
  void finalizeArchiveCreation(Archiver paramArchiver) throws ArchiverException;
  
  void finalizeArchiveExtraction(UnArchiver paramUnArchiver) throws ArchiverException;
  
  List getVirtualFiles();
}
