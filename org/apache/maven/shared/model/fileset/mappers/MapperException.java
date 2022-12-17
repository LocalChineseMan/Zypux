package org.apache.maven.shared.model.fileset.mappers;

public class MapperException extends Exception {
  static final long serialVersionUID = 20064059145045044L;
  
  public MapperException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public MapperException(String message) {
    super(message);
  }
}
