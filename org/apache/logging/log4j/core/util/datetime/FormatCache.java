package org.apache.logging.log4j.core.util.datetime;

import java.util.Arrays;

class MultipartKey {
  private final Object[] keys;
  
  private int hashCode;
  
  public MultipartKey(Object... keys) {
    this.keys = keys;
  }
  
  public boolean equals(Object obj) {
    return Arrays.equals(this.keys, ((MultipartKey)obj).keys);
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int rc = 0;
      for (Object key : this.keys) {
        if (key != null)
          rc = rc * 7 + key.hashCode(); 
      } 
      this.hashCode = rc;
    } 
    return this.hashCode;
  }
  
  private static class FormatCache {}
}
