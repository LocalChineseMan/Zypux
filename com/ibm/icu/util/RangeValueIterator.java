package com.ibm.icu.util;

public interface RangeValueIterator {
  boolean next(Element paramElement);
  
  void reset();
  
  public static class Element {
    public int start;
    
    public int limit;
    
    public int value;
  }
}
