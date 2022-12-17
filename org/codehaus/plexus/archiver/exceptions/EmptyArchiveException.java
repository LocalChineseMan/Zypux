package org.codehaus.plexus.archiver.exceptions;

import org.codehaus.plexus.archiver.ArchiverException;

public class EmptyArchiveException extends ArchiverException {
  public EmptyArchiveException(String message) {
    super(message);
  }
  
  public EmptyArchiveException(String message, Throwable cause) {
    super(message, cause);
  }
}
