package org.apache.maven.shared.model.fileset.mappers;

public interface FileNameMapper {
  void setFrom(String paramString);
  
  void setTo(String paramString);
  
  String mapFileName(String paramString);
}
