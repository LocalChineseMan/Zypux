package com.ibm.icu.text;

public interface Replaceable {
  int length();
  
  char charAt(int paramInt);
  
  int char32At(int paramInt);
  
  void getChars(int paramInt1, int paramInt2, char[] paramArrayOfchar, int paramInt3);
  
  void replace(int paramInt1, int paramInt2, String paramString);
  
  void replace(int paramInt1, int paramInt2, char[] paramArrayOfchar, int paramInt3, int paramInt4);
  
  void copy(int paramInt1, int paramInt2, int paramInt3);
  
  boolean hasMetaData();
}
