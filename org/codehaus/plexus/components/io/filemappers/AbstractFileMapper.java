package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;

public abstract class AbstractFileMapper implements FileMapper {
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    if (pName == null || pName.length() == 0)
      throw new IllegalArgumentException("The source name must not be null."); 
    return pName;
  }
}
