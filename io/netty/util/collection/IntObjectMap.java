package io.netty.util.collection;

public interface IntObjectMap<V> {
  V get(int paramInt);
  
  V put(int paramInt, V paramV);
  
  void putAll(IntObjectMap<V> paramIntObjectMap);
  
  V remove(int paramInt);
  
  int size();
  
  boolean isEmpty();
  
  void clear();
  
  boolean containsKey(int paramInt);
  
  boolean containsValue(V paramV);
  
  Iterable<Entry<V>> entries();
  
  int[] keys();
  
  V[] values(Class<V> paramClass);
  
  public static interface Entry<V> {
    int key();
    
    V value();
    
    void setValue(V param1V);
  }
}
