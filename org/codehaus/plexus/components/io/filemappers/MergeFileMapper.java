package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("merge")
public class MergeFileMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "merge";
  
  private String targetName;
  
  public void setTargetName(String pName) {
    if (pName == null)
      throw new IllegalArgumentException("The target name is null."); 
    if (pName.length() == 0)
      throw new IllegalArgumentException("The target name is empty."); 
    this.targetName = pName;
  }
  
  public String getTargetName() {
    return this.targetName;
  }
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    String name = getTargetName();
    if (name == null)
      throw new IllegalStateException("The target file name has not been set."); 
    super.getMappedFileName(pName);
    return name;
  }
}
