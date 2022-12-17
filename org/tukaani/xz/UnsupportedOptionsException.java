package org.tukaani.xz;

public class UnsupportedOptionsException extends XZIOException {
  private static final long serialVersionUID = 3L;
  
  public UnsupportedOptionsException() {}
  
  public UnsupportedOptionsException(String paramString) {
    super(paramString);
  }
}
