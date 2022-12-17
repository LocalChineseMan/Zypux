package org.apache.maven.shared.model.fileset.mappers;

public class MergingMapper implements FileNameMapper {
  private String mergedFile = null;
  
  public void setFrom(String from) {}
  
  public void setTo(String to) {
    this.mergedFile = to;
  }
  
  public String mapFileName(String sourceFileName) {
    return this.mergedFile;
  }
}
