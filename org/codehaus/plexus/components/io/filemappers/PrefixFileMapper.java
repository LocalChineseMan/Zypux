package org.codehaus.plexus.components.io.filemappers;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("prefix")
public class PrefixFileMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "prefix";
  
  private String prefix;
  
  @Nonnull
  public String getMappedFileName(@Nonnull String name) {
    String s = super.getMappedFileName(name);
    return getMappedFileName(this.prefix, s);
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  
  public static String getMappedFileName(String prefix, String name) {
    if (prefix == null || prefix.length() == 0)
      return name; 
    return prefix + name;
  }
}
