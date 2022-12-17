package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;

abstract class PowerOfTwoStriped<L> extends Striped<L> {
  final int mask;
  
  PowerOfTwoStriped(int stripes) {
    Preconditions.checkArgument((stripes > 0), "Stripes must be positive");
    this.mask = (stripes > 1073741824) ? -1 : (Striped.access$200(stripes) - 1);
  }
  
  final int indexFor(Object key) {
    int hash = Striped.access$300(key.hashCode());
    return hash & this.mask;
  }
  
  public final L get(Object key) {
    return (L)getAt(indexFor(key));
  }
}
