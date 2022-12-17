package org.tukaani.xz.delta;

abstract class DeltaCoder {
  static final int DISTANCE_MIN = 1;
  
  static final int DISTANCE_MAX = 256;
  
  static final int DISTANCE_MASK = 255;
  
  final int distance;
  
  final byte[] history = new byte[256];
  
  int pos = 0;
  
  DeltaCoder(int paramInt) {
    if (paramInt < 1 || paramInt > 256)
      throw new IllegalArgumentException(); 
    this.distance = paramInt;
  }
}
