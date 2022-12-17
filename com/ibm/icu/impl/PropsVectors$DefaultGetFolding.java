package com.ibm.icu.impl;

class DefaultGetFoldingOffset implements Trie.DataManipulate {
  private DefaultGetFoldingOffset() {}
  
  public int getFoldingOffset(int value) {
    return value;
  }
}
