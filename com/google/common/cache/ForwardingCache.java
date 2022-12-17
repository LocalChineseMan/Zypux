package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public abstract class SimpleForwardingCache<K, V> extends ForwardingCache<K, V> {
  private final Cache<K, V> delegate;
  
  protected SimpleForwardingCache(Cache<K, V> delegate) {
    this.delegate = (Cache<K, V>)Preconditions.checkNotNull(delegate);
  }
  
  protected final Cache<K, V> delegate() {
    return this.delegate;
  }
  
  public static abstract class ForwardingCache {}
}
