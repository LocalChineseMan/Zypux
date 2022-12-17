package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("flatten")
public class FlattenFileMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "flatten";
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    String name = super.getMappedFileName(pName);
    int offset = pName.lastIndexOf('/');
    if (offset >= 0)
      name = name.substring(offset + 1); 
    offset = pName.lastIndexOf('\\');
    if (offset >= 0)
      name = name.substring(offset + 1); 
    return name;
  }
}
