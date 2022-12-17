package org.codehaus.plexus.archiver.tar;

public enum TarLongFileMode {
  warn, fail, truncate, gnu, omit, posix, posix_warn;
  
  public boolean isTruncateMode() {
    return truncate.equals(this);
  }
  
  public boolean isWarnMode() {
    return warn.equals(this);
  }
  
  public boolean isGnuMode() {
    return gnu.equals(this);
  }
  
  public boolean isFailMode() {
    return fail.equals(this);
  }
  
  public boolean isOmitMode() {
    return omit.equals(this);
  }
  
  public boolean isPosixMode() {
    return posix.equals(this);
  }
  
  public boolean isPosixWarnMode() {
    return posix_warn.equals(this);
  }
}
