package org.codehaus.plexus.components.io.fileselectors;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("all")
public class AllFilesFileSelector implements FileSelector {
  public static final String ROLE_HINT = "all";
  
  public boolean isSelected(@Nonnull FileInfo fileInfo) {
    return true;
  }
}
