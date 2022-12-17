package com.ibm.icu.text;

interface UnicodeReplacer {
  int replace(Replaceable paramReplaceable, int paramInt1, int paramInt2, int[] paramArrayOfint);
  
  String toReplacerPattern(boolean paramBoolean);
  
  void addReplacementSetTo(UnicodeSet paramUnicodeSet);
}
