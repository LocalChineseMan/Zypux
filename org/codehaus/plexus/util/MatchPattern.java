package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MatchPattern {
  private final java.lang.String source;
  
  private final java.lang.String regexPattern;
  
  private final java.lang.String separator;
  
  private final java.lang.String[] tokenized;
  
  private final char[][] tokenizedChar;
  
  private MatchPattern(java.lang.String source, java.lang.String separator) {
    this
      
      .regexPattern = SelectorUtils.isRegexPrefixedPattern(source) ? source.substring("%regex[".length(), source.length() - "]".length()) : null;
    this
      
      .source = SelectorUtils.isAntPrefixedPattern(source) ? source.substring("%ant[".length(), source.length() - "]".length()) : source;
    this.separator = separator;
    this.tokenized = tokenizePathToString(this.source, separator);
    this.tokenizedChar = new char[this.tokenized.length][];
    for (int i = 0; i < this.tokenized.length; i++)
      this.tokenizedChar[i] = this.tokenized[i].toCharArray(); 
  }
  
  public boolean matchPath(java.lang.String str, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return str.matches(this.regexPattern); 
    return SelectorUtils.matchAntPathPattern(this, str, this.separator, isCaseSensitive);
  }
  
  boolean matchPath(java.lang.String str, char[][] strDirs, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return str.matches(this.regexPattern); 
    return SelectorUtils.matchAntPathPattern(getTokenizedPathChars(), strDirs, isCaseSensitive);
  }
  
  public boolean matchPatternStart(java.lang.String str, boolean isCaseSensitive) {
    if (this.regexPattern != null)
      return true; 
    java.lang.String altStr = str.replace('\\', '/');
    return (SelectorUtils.matchAntPathPatternStart(this, str, File.separator, isCaseSensitive) || 
      SelectorUtils.matchAntPathPatternStart(this, altStr, "/", isCaseSensitive));
  }
  
  public java.lang.String[] getTokenizedPathString() {
    return this.tokenized;
  }
  
  public char[][] getTokenizedPathChars() {
    return this.tokenizedChar;
  }
  
  public boolean startsWith(java.lang.String string) {
    return this.source.startsWith(string);
  }
  
  static java.lang.String[] tokenizePathToString(java.lang.String path, java.lang.String separator) {
    List<java.lang.String> ret = new ArrayList<>();
    StringTokenizer st = new StringTokenizer(path, separator);
    while (st.hasMoreTokens())
      ret.add(st.nextToken()); 
    return ret.<java.lang.String>toArray(new java.lang.String[0]);
  }
  
  static char[][] tokenizePathToCharArray(java.lang.String path, java.lang.String separator) {
    java.lang.String[] tokenizedName = tokenizePathToString(path, separator);
    char[][] tokenizedNameChar = new char[tokenizedName.length][];
    for (int i = 0; i < tokenizedName.length; i++)
      tokenizedNameChar[i] = tokenizedName[i].toCharArray(); 
    return tokenizedNameChar;
  }
  
  public static MatchPattern fromString(java.lang.String source) {
    return new MatchPattern(source, File.separator);
  }
}
