package org.lwjgl.util;

public interface WritablePoint {
  void setLocation(int paramInt1, int paramInt2);
  
  void setLocation(ReadablePoint paramReadablePoint);
  
  void setX(int paramInt);
  
  void setY(int paramInt);
}
