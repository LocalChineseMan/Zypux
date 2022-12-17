package org.apache.maven.shared.model.fileset;

import java.io.Serializable;

public class FileSet extends SetBase implements Serializable {
  private String directory;
  
  private String lineEnding;
  
  private String modelEncoding = "UTF-8";
  
  public String getDirectory() {
    return this.directory;
  }
  
  public String getLineEnding() {
    return this.lineEnding;
  }
  
  public String getModelEncoding() {
    return this.modelEncoding;
  }
  
  public void setDirectory(String directory) {
    this.directory = directory;
  }
  
  public void setLineEnding(String lineEnding) {
    this.lineEnding = lineEnding;
  }
  
  public void setModelEncoding(String modelEncoding) {
    this.modelEncoding = modelEncoding;
  }
}
