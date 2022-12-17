package com.jhlabs.image;

public class InvertFilter extends PointFilter {
  public InvertFilter() {
    this.canFilterIndexColorModel = true;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int a = rgb & 0xFF000000;
    return a | (rgb ^ 0xFFFFFFFF) & 0xFFFFFF;
  }
  
  public String toString() {
    return "Colors/Invert";
  }
}
