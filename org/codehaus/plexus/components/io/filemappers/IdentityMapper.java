package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("identity")
public class IdentityMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "identity";
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    if (pName == null || pName.length() == 0)
      throw new IllegalArgumentException("The source name must not be null."); 
    return pName;
  }
}
