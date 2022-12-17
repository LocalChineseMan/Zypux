package com.google.common.collect;

abstract class TableSet<T> extends Sets.ImprovedAbstractSet<T> {
  private TableSet() {}
  
  public boolean isEmpty() {
    return StandardTable.this.backingMap.isEmpty();
  }
  
  public void clear() {
    StandardTable.this.backingMap.clear();
  }
  
  private abstract class StandardTable {}
}
