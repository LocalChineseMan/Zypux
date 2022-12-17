package org.tukaani.xz.lz;

public final class Matches {
  public final int[] len;
  
  public final int[] dist;
  
  public int count = 0;
  
  Matches(int paramInt) {
    this.len = new int[paramInt];
    this.dist = new int[paramInt];
  }
}
