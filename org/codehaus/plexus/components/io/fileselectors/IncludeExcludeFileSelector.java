package org.codehaus.plexus.components.io.fileselectors;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.MatchPatterns;
import org.codehaus.plexus.util.SelectorUtils;

@Named("standard")
public class IncludeExcludeFileSelector implements FileSelector {
  public static final String ROLE_HINT = "standard";
  
  private static final MatchPatterns ALL_INCLUDES = MatchPatterns.from(new String[] { getCanonicalName("**/*") });
  
  private static final MatchPatterns ZERO_EXCLUDES = MatchPatterns.from(new String[0]);
  
  private boolean isCaseSensitive = true;
  
  private boolean useDefaultExcludes = true;
  
  private String[] includes;
  
  private String[] excludes;
  
  private MatchPatterns computedIncludes = ALL_INCLUDES;
  
  private MatchPatterns computedExcludes = ZERO_EXCLUDES;
  
  protected boolean isExcluded(@Nonnull String name) {
    return this.computedExcludes.matches(name, this.isCaseSensitive);
  }
  
  public void setIncludes(@Nullable String[] includes) {
    this.includes = includes;
    if (includes == null) {
      this.computedIncludes = ALL_INCLUDES;
    } else {
      String[] cleaned = new String[includes.length];
      for (int i = 0; i < includes.length; i++)
        cleaned[i] = asPattern(includes[i]); 
      this.computedIncludes = MatchPatterns.from(cleaned);
    } 
  }
  
  @Nonnull
  private static String getCanonicalName(@Nonnull String pName) {
    return pName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }
  
  private String asPattern(@Nonnull String pPattern) {
    String pattern = getCanonicalName(pPattern.trim());
    if (pattern.endsWith(File.separator))
      pattern = pattern + "**"; 
    return pattern;
  }
  
  @Nullable
  public String[] getIncludes() {
    return this.includes;
  }
  
  public void setExcludes(@Nullable String[] excludes) {
    this.excludes = excludes;
    String[] defaultExcludes = this.useDefaultExcludes ? FileUtils.getDefaultExcludes() : new String[0];
    if (excludes == null) {
      this.computedExcludes = MatchPatterns.from(defaultExcludes);
    } else {
      String[] temp = new String[excludes.length + defaultExcludes.length];
      for (int i = 0; i < excludes.length; i++)
        temp[i] = asPattern(excludes[i]); 
      if (defaultExcludes.length > 0)
        System.arraycopy(defaultExcludes, 0, temp, excludes.length, defaultExcludes.length); 
      this.computedExcludes = MatchPatterns.from(temp);
    } 
  }
  
  @Nullable
  public String[] getExcludes() {
    return this.excludes;
  }
  
  protected boolean matchPath(@Nonnull String pattern, @Nonnull String name, boolean isCaseSensitive) {
    return SelectorUtils.matchPath(pattern, name, isCaseSensitive);
  }
  
  protected boolean isIncluded(@Nonnull String name) {
    return this.computedIncludes.matches(name, this.isCaseSensitive);
  }
  
  public boolean isSelected(@Nonnull FileInfo fileInfo) {
    String name = getCanonicalName(fileInfo.getName());
    return (isIncluded(name) && !isExcluded(name));
  }
  
  public boolean isCaseSensitive() {
    return this.isCaseSensitive;
  }
  
  public void setCaseSensitive(boolean caseSensitive) {
    this.isCaseSensitive = caseSensitive;
  }
  
  public boolean isUseDefaultExcludes() {
    return this.useDefaultExcludes;
  }
  
  public void setUseDefaultExcludes(boolean pUseDefaultExcludes) {
    this.useDefaultExcludes = pUseDefaultExcludes;
    setExcludes(this.excludes);
  }
}
