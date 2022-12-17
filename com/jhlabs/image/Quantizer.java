package com.jhlabs.image;

public interface Quantizer {
  void setup(int paramInt);
  
  void addPixels(int[] paramArrayOfint, int paramInt1, int paramInt2);
  
  int[] buildColorTable();
  
  int getIndexForColor(int paramInt);
}
