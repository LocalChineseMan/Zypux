package org.codehaus.plexus.archiver;

public class ArchiverException extends RuntimeException {
  public ArchiverException(String message) {
    super(message);
  }
  
  public ArchiverException(String message, Throwable cause) {
    super(message, cause);
  }
}
