package com.ibm.icu.text;

import java.text.ParsePosition;

public interface SymbolTable {
  public static final char SYMBOL_REF = '$';
  
  char[] lookup(String paramString);
  
  UnicodeMatcher lookupMatcher(int paramInt);
  
  String parseReference(String paramString, ParsePosition paramParsePosition, int paramInt);
}
