package org.apache.maven.shared.model.fileset.mappers;

public class IdentityMapper implements FileNameMapper {
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public String mapFileName(String sourceFileName) {
    return sourceFileName;
  }
}
