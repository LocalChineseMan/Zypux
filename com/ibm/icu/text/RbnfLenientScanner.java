package com.ibm.icu.text;

public interface RbnfLenientScanner {
  boolean allIgnorable(String paramString);
  
  int prefixLength(String paramString1, String paramString2);
  
  int[] findText(String paramString1, String paramString2, int paramInt);
}
