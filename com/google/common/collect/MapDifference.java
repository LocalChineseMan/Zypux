package com.google.common.collect;

import javax.annotation.Nullable;

public interface ValueDifference<V> {
  V leftValue();
  
  V rightValue();
  
  boolean equals(@Nullable Object paramObject);
  
  int hashCode();
  
  public static interface MapDifference {}
}
