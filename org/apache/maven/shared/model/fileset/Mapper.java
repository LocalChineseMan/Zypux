package org.apache.maven.shared.model.fileset;

import java.io.Serializable;

public class Mapper implements Serializable {
  private String type = "identity";
  
  private String from;
  
  private String to;
  
  private String classname;
  
  public String getClassname() {
    return this.classname;
  }
  
  public String getFrom() {
    return this.from;
  }
  
  public String getTo() {
    return this.to;
  }
  
  public String getType() {
    return this.type;
  }
  
  public void setClassname(String classname) {
    this.classname = classname;
  }
  
  public void setFrom(String from) {
    this.from = from;
  }
  
  public void setTo(String to) {
    this.to = to;
  }
  
  public void setType(String type) {
    this.type = type;
  }
}
