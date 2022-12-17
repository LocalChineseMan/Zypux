package org.apache.commons.lang3.text;

import java.util.Map;

class MapStrLookup<V> extends StrLookup<V> {
  private final Map<String, V> map;
  
  MapStrLookup(Map<String, V> map) {
    this.map = map;
  }
  
  public String lookup(String key) {
    if (this.map == null)
      return null; 
    Object obj = this.map.get(key);
    if (obj == null)
      return null; 
    return obj.toString();
  }
  
  static class StrLookup {}
}
