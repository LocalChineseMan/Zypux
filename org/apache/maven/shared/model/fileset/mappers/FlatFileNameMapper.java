package org.apache.maven.shared.model.fileset.mappers;

import java.io.File;

public class FlatFileNameMapper implements FileNameMapper {
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public String mapFileName(String sourceFileName) {
    return (new File(sourceFileName)).getName();
  }
}
