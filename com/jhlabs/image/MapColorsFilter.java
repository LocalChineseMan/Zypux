package com.jhlabs.image;

public class MapColorsFilter extends PointFilter {
  private int oldColor;
  
  private int newColor;
  
  public MapColorsFilter() {
    this(-1, -16777216);
  }
  
  public MapColorsFilter(int oldColor, int newColor) {
    this.canFilterIndexColorModel = true;
    this.oldColor = oldColor;
    this.newColor = newColor;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    if (rgb == this.oldColor)
      return this.newColor; 
    return rgb;
  }
}
