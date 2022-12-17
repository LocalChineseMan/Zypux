package com.ibm.icu.text;

import java.text.CharacterIterator;
import java.util.Stack;

interface LanguageBreakEngine {
  boolean handles(int paramInt1, int paramInt2);
  
  int findBreaks(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, Stack<Integer> paramStack);
}
