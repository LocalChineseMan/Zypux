package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("suffix")
public class SuffixFileMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "suffix";
  
  private String suffix;
  
  public String getSuffix() {
    return this.suffix;
  }
  
  public void setSuffix(String suffix) {
    if (suffix == null)
      throw new IllegalArgumentException("The suffix is null."); 
    this.suffix = suffix;
  }
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    String name = super.getMappedFileName(pName);
    if (this.suffix == null)
      throw new IllegalStateException("The suffix has not been set."); 
    int dirSep = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
    String filename = (dirSep > 0) ? name.substring(dirSep + 1) : name;
    String dirname = (dirSep > 0) ? name.substring(0, dirSep + 1) : "";
    if (filename.contains(".")) {
      String beforeExtension = filename.substring(0, filename.indexOf('.'));
      String afterExtension = filename.substring(filename.indexOf('.') + 1);
      return dirname + beforeExtension + this.suffix + "." + afterExtension;
    } 
    return name + this.suffix;
  }
}
