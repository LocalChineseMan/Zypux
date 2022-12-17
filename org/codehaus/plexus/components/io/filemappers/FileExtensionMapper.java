package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("fileExtension")
public class FileExtensionMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "fileExtension";
  
  private String targetExtension;
  
  public void setTargetExtension(String pTargetExtension) {
    if (pTargetExtension == null)
      throw new IllegalArgumentException("The target extension is null."); 
    if (pTargetExtension.length() == 0)
      throw new IllegalArgumentException("The target extension is empty."); 
    if (pTargetExtension.charAt(0) == '.') {
      this.targetExtension = pTargetExtension;
    } else {
      this.targetExtension = '.' + pTargetExtension;
    } 
  }
  
  public String getTargetExtension() {
    return this.targetExtension;
  }
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    String ext = getTargetExtension();
    if (ext == null)
      throw new IllegalStateException("The target extension has not been set."); 
    String name = super.getMappedFileName(pName);
    int dirSep = Math.max(pName.lastIndexOf('/'), pName.lastIndexOf('\\'));
    int offset = pName.lastIndexOf('.');
    if (offset <= dirSep)
      return name + ext; 
    return name.substring(0, offset) + ext;
  }
}
