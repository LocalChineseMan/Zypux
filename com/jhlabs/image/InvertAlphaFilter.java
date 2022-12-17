package com.jhlabs.image;

public class InvertAlphaFilter extends PointFilter {
  public InvertAlphaFilter() {
    this.canFilterIndexColorModel = true;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    return rgb ^ 0xFF000000;
  }
  
  public String toString() {
    return "Alpha/Invert";
  }
}
