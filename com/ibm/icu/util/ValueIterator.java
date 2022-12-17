package com.ibm.icu.util;

public interface ValueIterator {
  boolean next(Element paramElement);
  
  void reset();
  
  void setRange(int paramInt1, int paramInt2);
  
  public static final class Element {
    public int integer;
    
    public Object value;
  }
}
