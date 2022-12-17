package com.jhlabs.image;

public class JavaLnFFilter extends PointFilter {
  public int filterRGB(int x, int y, int rgb) {
    if ((x & 0x1) == (y & 0x1))
      return rgb; 
    return ImageMath.mixColors(0.25F, -6710887, rgb);
  }
  
  public String toString() {
    return "Stylize/Java L&F Stipple";
  }
}
