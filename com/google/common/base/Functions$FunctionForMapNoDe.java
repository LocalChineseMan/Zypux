package com.google.common.base;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {
  final Map<K, V> map;
  
  private static final long serialVersionUID = 0L;
  
  FunctionForMapNoDefault(Map<K, V> map) {
    this.map = Preconditions.<Map<K, V>>checkNotNull(map);
  }
  
  public V apply(@Nullable K key) {
    V result = this.map.get(key);
    Preconditions.checkArgument((result != null || this.map.containsKey(key)), "Key '%s' not present in map", new Object[] { key });
    return result;
  }
  
  public boolean equals(@Nullable Object o) {
    if (o instanceof FunctionForMapNoDefault) {
      FunctionForMapNoDefault<?, ?> that = (FunctionForMapNoDefault<?, ?>)o;
      return this.map.equals(that.map);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.map.hashCode();
  }
  
  public String toString() {
    return "forMap(" + this.map + ")";
  }
}
