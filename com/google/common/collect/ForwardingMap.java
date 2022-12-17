package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Map;

@Beta
public class StandardKeySet extends Maps.KeySet<K, V> {
  public StandardKeySet() {
    super((Map<K, V>)paramForwardingMap);
  }
  
  protected class ForwardingMap {}
}
