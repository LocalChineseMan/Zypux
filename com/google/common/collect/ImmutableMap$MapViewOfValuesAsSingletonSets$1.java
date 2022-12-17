package com.google.common.collect;

import java.util.Map;

class null extends AbstractMapEntry<K, ImmutableSet<V>> {
  public K getKey() {
    return (K)backingEntry.getKey();
  }
  
  public ImmutableSet<V> getValue() {
    return ImmutableSet.of((V)backingEntry.getValue());
  }
}
