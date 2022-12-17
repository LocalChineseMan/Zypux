package org.lwjgl.util;

public interface WritableDimension {
  void setSize(int paramInt1, int paramInt2);
  
  void setSize(ReadableDimension paramReadableDimension);
  
  void setHeight(int paramInt);
  
  void setWidth(int paramInt);
}
