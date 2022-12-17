package com.ibm.icu.impl;

public interface ICUCache<K, V> {
  public static final int SOFT = 0;
  
  public static final int WEAK = 1;
  
  public static final Object NULL = new Object();
  
  void clear();
  
  void put(K paramK, V paramV);
  
  V get(Object paramObject);
}
