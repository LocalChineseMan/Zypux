package org.codehaus.plexus.components.io.filemappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.inject.Named;

@Named("regexp")
public class RegExpFileMapper extends AbstractFileMapper {
  public static final String ROLE_HINT = "regexp";
  
  private Pattern pattern;
  
  private String replacement;
  
  private boolean replaceAll;
  
  public void setPattern(String pPattern) {
    this.pattern = Pattern.compile(pPattern);
  }
  
  public String getPattern() {
    return (this.pattern == null) ? null : this.pattern.pattern();
  }
  
  public void setReplacement(String pReplacement) {
    this.replacement = pReplacement;
  }
  
  public String getReplacement() {
    return this.replacement;
  }
  
  public boolean getReplaceAll() {
    return this.replaceAll;
  }
  
  public void setReplaceAll(boolean pReplaceAll) {
    this.replaceAll = pReplaceAll;
  }
  
  @Nonnull
  public String getMappedFileName(@Nonnull String pName) {
    String name = super.getMappedFileName(pName);
    if (this.pattern == null)
      throw new IllegalStateException("The regular expression pattern has not been set."); 
    if (this.replacement == null)
      throw new IllegalStateException("The pattern replacement string has not been set."); 
    Matcher matcher = this.pattern.matcher(name);
    if (!matcher.find())
      return name; 
    if (!getReplaceAll())
      return matcher.replaceFirst(this.replacement); 
    return matcher.replaceAll(this.replacement);
  }
}
