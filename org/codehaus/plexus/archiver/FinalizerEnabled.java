package org.codehaus.plexus.archiver;

import java.util.List;

public interface FinalizerEnabled {
  void addArchiveFinalizer(ArchiveFinalizer paramArchiveFinalizer);
  
  void setArchiveFinalizers(List<ArchiveFinalizer> paramList);
}
