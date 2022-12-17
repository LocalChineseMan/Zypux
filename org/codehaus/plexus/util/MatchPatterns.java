package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MatchPatterns {
  private final MatchPattern[] patterns;
  
  private MatchPatterns(MatchPattern[] patterns) {
    this.patterns = patterns;
  }
  
  public boolean matches(java.lang.String name, boolean isCaseSensitive) {
    java.lang.String[] tokenized = MatchPattern.tokenizePathToString(name, File.separator);
    return matches(name, tokenized, isCaseSensitive);
  }
  
  public boolean matches(java.lang.String name, java.lang.String[] tokenizedName, boolean isCaseSensitive) {
    char[][] tokenizedNameChar = new char[tokenizedName.length][];
    for (int i = 0; i < tokenizedName.length; i++)
      tokenizedNameChar[i] = tokenizedName[i].toCharArray(); 
    return matches(name, tokenizedNameChar, isCaseSensitive);
  }
  
  public boolean matches(java.lang.String name, char[][] tokenizedNameChar, boolean isCaseSensitive) {
    for (MatchPattern pattern : this.patterns) {
      if (pattern.matchPath(name, tokenizedNameChar, isCaseSensitive))
        return true; 
    } 
    return false;
  }
  
  public boolean matchesPatternStart(java.lang.String name, boolean isCaseSensitive) {
    for (MatchPattern includesPattern : this.patterns) {
      if (includesPattern.matchPatternStart(name, isCaseSensitive))
        return true; 
    } 
    return false;
  }
  
  public static MatchPatterns from(java.lang.String... sources) {
    int length = sources.length;
    MatchPattern[] result = new MatchPattern[length];
    for (int i = 0; i < length; i++)
      result[i] = MatchPattern.fromString(sources[i]); 
    return new MatchPatterns(result);
  }
  
  public static MatchPatterns from(Iterable<java.lang.String> strings) {
    return new MatchPatterns(getMatchPatterns(strings));
  }
  
  private static MatchPattern[] getMatchPatterns(Iterable<java.lang.String> items) {
    List<MatchPattern> result = new ArrayList<>();
    for (java.lang.String string : items)
      result.add(MatchPattern.fromString(string)); 
    return result.<MatchPattern>toArray(new MatchPattern[0]);
  }
}
