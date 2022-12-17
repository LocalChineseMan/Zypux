package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractScanner implements Scanner {
  public static final java.lang.String[] DEFAULTEXCLUDES = new java.lang.String[] { 
      "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/RCS", "**/RCS/**", 
      "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/project.pj", "**/.svn", "**/.svn/**", "**/.arch-ids", "**/.arch-ids/**", "**/.bzr", "**/.bzr/**", 
      "**/.MySCMServerInfo", "**/.DS_Store", "**/.metadata", "**/.metadata/**", "**/.hg", "**/.hg/**", "**/.git", "**/.git/**", "**/BitKeeper", "**/BitKeeper/**", 
      "**/ChangeSet", "**/ChangeSet/**", "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail" };
  
  protected java.lang.String[] includes;
  
  private MatchPatterns includesPatterns;
  
  protected java.lang.String[] excludes;
  
  private MatchPatterns excludesPatterns;
  
  protected boolean isCaseSensitive = true;
  
  protected Comparator<java.lang.String> filenameComparator;
  
  public void setCaseSensitive(boolean isCaseSensitive) {
    this.isCaseSensitive = isCaseSensitive;
  }
  
  protected static boolean matchPatternStart(java.lang.String pattern, java.lang.String str) {
    return SelectorUtils.matchPatternStart(pattern, str);
  }
  
  protected static boolean matchPatternStart(java.lang.String pattern, java.lang.String str, boolean isCaseSensitive) {
    return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
  }
  
  protected static boolean matchPath(java.lang.String pattern, java.lang.String str) {
    return SelectorUtils.matchPath(pattern, str);
  }
  
  protected static boolean matchPath(java.lang.String pattern, java.lang.String str, boolean isCaseSensitive) {
    return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
  }
  
  public static boolean match(java.lang.String pattern, java.lang.String str) {
    return SelectorUtils.match(pattern, str);
  }
  
  protected static boolean match(java.lang.String pattern, java.lang.String str, boolean isCaseSensitive) {
    return SelectorUtils.match(pattern, str, isCaseSensitive);
  }
  
  public void setIncludes(java.lang.String[] includes) {
    if (includes == null) {
      this.includes = null;
    } else {
      List<java.lang.String> list = new ArrayList<>(includes.length);
      for (java.lang.String include : includes) {
        if (include != null)
          list.add(normalizePattern(include)); 
      } 
      this.includes = list.<java.lang.String>toArray(new java.lang.String[0]);
    } 
  }
  
  public void setExcludes(java.lang.String[] excludes) {
    if (excludes == null) {
      this.excludes = null;
    } else {
      List<java.lang.String> list = new ArrayList<>(excludes.length);
      for (java.lang.String exclude : excludes) {
        if (exclude != null)
          list.add(normalizePattern(exclude)); 
      } 
      this.excludes = list.<java.lang.String>toArray(new java.lang.String[0]);
    } 
  }
  
  private java.lang.String normalizePattern(java.lang.String pattern) {
    pattern = pattern.trim();
    if (pattern.startsWith("%regex[")) {
      if (File.separatorChar == '\\') {
        pattern = StringUtils.replace(pattern, "/", "\\\\");
      } else {
        pattern = StringUtils.replace(pattern, "\\\\", "/");
      } 
    } else {
      pattern = pattern.replace((File.separatorChar == '/') ? 92 : 47, File.separatorChar);
      if (pattern.endsWith(File.separator))
        pattern = pattern + "**"; 
    } 
    return pattern;
  }
  
  protected boolean isIncluded(java.lang.String name) {
    return this.includesPatterns.matches(name, this.isCaseSensitive);
  }
  
  protected boolean isIncluded(java.lang.String name, java.lang.String[] tokenizedName) {
    return this.includesPatterns.matches(name, tokenizedName, this.isCaseSensitive);
  }
  
  protected boolean isIncluded(java.lang.String name, char[][] tokenizedName) {
    return this.includesPatterns.matches(name, tokenizedName, this.isCaseSensitive);
  }
  
  protected boolean couldHoldIncluded(java.lang.String name) {
    return this.includesPatterns.matchesPatternStart(name, this.isCaseSensitive);
  }
  
  protected boolean isExcluded(java.lang.String name) {
    return this.excludesPatterns.matches(name, this.isCaseSensitive);
  }
  
  protected boolean isExcluded(java.lang.String name, java.lang.String[] tokenizedName) {
    return this.excludesPatterns.matches(name, tokenizedName, this.isCaseSensitive);
  }
  
  protected boolean isExcluded(java.lang.String name, char[][] tokenizedName) {
    return this.excludesPatterns.matches(name, tokenizedName, this.isCaseSensitive);
  }
  
  public void addDefaultExcludes() {
    int excludesLength = (this.excludes == null) ? 0 : this.excludes.length;
    java.lang.String[] newExcludes = new java.lang.String[excludesLength + DEFAULTEXCLUDES.length];
    if (excludesLength > 0)
      System.arraycopy(this.excludes, 0, newExcludes, 0, excludesLength); 
    for (int i = 0; i < DEFAULTEXCLUDES.length; i++)
      newExcludes[i + excludesLength] = DEFAULTEXCLUDES[i].replace('/', File.separatorChar); 
    this.excludes = newExcludes;
  }
  
  protected void setupDefaultFilters() {
    if (this.includes == null) {
      this.includes = new java.lang.String[1];
      this.includes[0] = "**";
    } 
    if (this.excludes == null)
      this.excludes = new java.lang.String[0]; 
  }
  
  protected void setupMatchPatterns() {
    this.includesPatterns = MatchPatterns.from(this.includes);
    this.excludesPatterns = MatchPatterns.from(this.excludes);
  }
  
  public void setFilenameComparator(Comparator<java.lang.String> filenameComparator) {
    this.filenameComparator = filenameComparator;
  }
}
