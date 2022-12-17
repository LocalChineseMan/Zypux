package org.apache.maven.shared.model.fileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SetBase implements Serializable {
  private boolean followSymlinks = false;
  
  private String outputDirectory;
  
  private boolean useDefaultExcludes = true;
  
  private List<String> includes;
  
  private List<String> excludes;
  
  private String fileMode = "0644";
  
  private String directoryMode = "0755";
  
  private Mapper mapper = new Mapper();
  
  public void addExclude(String string) {
    getExcludes().add(string);
  }
  
  public void addInclude(String string) {
    getIncludes().add(string);
  }
  
  public String getDirectoryMode() {
    return this.directoryMode;
  }
  
  public List<String> getExcludes() {
    if (this.excludes == null)
      this.excludes = new ArrayList<>(); 
    return this.excludes;
  }
  
  public String getFileMode() {
    return this.fileMode;
  }
  
  public List<String> getIncludes() {
    if (this.includes == null)
      this.includes = new ArrayList<>(); 
    return this.includes;
  }
  
  public Mapper getMapper() {
    return this.mapper;
  }
  
  public String getOutputDirectory() {
    return this.outputDirectory;
  }
  
  public boolean isFollowSymlinks() {
    return this.followSymlinks;
  }
  
  public boolean isUseDefaultExcludes() {
    return this.useDefaultExcludes;
  }
  
  public void removeExclude(String string) {
    getExcludes().remove(string);
  }
  
  public void removeInclude(String string) {
    getIncludes().remove(string);
  }
  
  public void setDirectoryMode(String directoryMode) {
    this.directoryMode = directoryMode;
  }
  
  public void setExcludes(List<String> excludes) {
    this.excludes = excludes;
  }
  
  public void setFileMode(String fileMode) {
    this.fileMode = fileMode;
  }
  
  public void setFollowSymlinks(boolean followSymlinks) {
    this.followSymlinks = followSymlinks;
  }
  
  public void setIncludes(List<String> includes) {
    this.includes = includes;
  }
  
  public void setMapper(Mapper mapper) {
    this.mapper = mapper;
  }
  
  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = outputDirectory;
  }
  
  public void setUseDefaultExcludes(boolean useDefaultExcludes) {
    this.useDefaultExcludes = useDefaultExcludes;
  }
  
  public String[] getIncludesArray() {
    String[] includesArry = null;
    List<String> includes = getIncludes();
    if (includes != null && !includes.isEmpty()) {
      includesArry = includes.<String>toArray(new String[0]);
    } else if (includes != null) {
      includesArry = new String[0];
    } 
    return includesArry;
  }
  
  public String[] getExcludesArray() {
    String[] excludesArry = null;
    List<String> excludes = getExcludes();
    if (excludes != null && !excludes.isEmpty()) {
      excludesArry = excludes.<String>toArray(new String[0]);
    } else if (excludes != null) {
      excludesArry = new String[0];
    } 
    return excludesArry;
  }
}
