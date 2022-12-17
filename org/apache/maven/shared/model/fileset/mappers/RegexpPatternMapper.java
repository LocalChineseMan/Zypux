package org.apache.maven.shared.model.fileset.mappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpPatternMapper implements FileNameMapper {
  private Pattern fromPattern;
  
  private String toReplaceExpression;
  
  public void setFrom(String from) {
    this.fromPattern = Pattern.compile(from);
  }
  
  public void setTo(String to) {
    this.toReplaceExpression = to;
  }
  
  public String mapFileName(String sourceFileName) {
    Matcher matcher = this.fromPattern.matcher(sourceFileName);
    if (!matcher.find())
      return sourceFileName; 
    return matcher.replaceFirst(this.toReplaceExpression);
  }
}
