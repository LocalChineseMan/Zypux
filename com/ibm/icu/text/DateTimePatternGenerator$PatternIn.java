package com.ibm.icu.text;

public final class PatternInfo {
  public static final int OK = 0;
  
  public static final int BASE_CONFLICT = 1;
  
  public static final int CONFLICT = 2;
  
  public int status;
  
  public String conflictingPattern;
}
