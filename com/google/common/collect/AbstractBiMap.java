package com.google.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

class ValueSet extends ForwardingSet<V> {
  final Set<V> valuesDelegate = AbstractBiMap.this.inverse.keySet();
  
  protected Set<V> delegate() {
    return this.valuesDelegate;
  }
  
  public Iterator<V> iterator() {
    return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
  }
  
  public Object[] toArray() {
    return standardToArray();
  }
  
  public <T> T[] toArray(T[] array) {
    return (T[])standardToArray((Object[])array);
  }
  
  public String toString() {
    return standardToString();
  }
  
  private ValueSet() {}
  
  private class AbstractBiMap {}
}
