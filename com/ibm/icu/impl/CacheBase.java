package com.ibm.icu.impl;

public abstract class CacheBase<K, V, D> {
  public abstract V getInstance(K paramK, D paramD);
  
  protected abstract V createInstance(K paramK, D paramD);
}
