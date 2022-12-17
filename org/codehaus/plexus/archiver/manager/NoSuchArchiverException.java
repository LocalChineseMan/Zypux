package org.codehaus.plexus.archiver.manager;

public class NoSuchArchiverException extends Exception {
  private final String archiverName;
  
  public NoSuchArchiverException(String archiverName) {
    super("No such archiver: '" + archiverName + "'.");
    this.archiverName = archiverName;
  }
  
  public String getArchiver() {
    return this.archiverName;
  }
}
