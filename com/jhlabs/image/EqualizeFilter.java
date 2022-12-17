package com.jhlabs.image;

import java.awt.Rectangle;
import java.io.Serializable;

public class EqualizeFilter extends WholeImageFilter implements Serializable {
  protected int[][] lut;
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    Histogram histogram = new Histogram(inPixels, width, height, 0, width);
    if (histogram.getNumSamples() > 0) {
      float scale = 255.0F / histogram.getNumSamples();
      this.lut = new int[3][256];
      for (int j = 0; j < 3; j++) {
        this.lut[j][0] = histogram.getFrequency(j, 0);
        int k;
        for (k = 1; k < 256; k++)
          this.lut[j][k] = this.lut[j][k - 1] + histogram.getFrequency(j, k); 
        for (k = 0; k < 256; k++)
          this.lut[j][k] = Math.round(this.lut[j][k] * scale); 
      } 
    } else {
      this.lut = (int[][])null;
    } 
    int i = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        inPixels[i] = filterRGB(x, y, inPixels[i]);
        i++;
      } 
    } 
    this.lut = (int[][])null;
    return inPixels;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    if (this.lut != null) {
      int a = rgb & 0xFF000000;
      int r = this.lut[0][rgb >> 16 & 0xFF];
      int g = this.lut[1][rgb >> 8 & 0xFF];
      int b = this.lut[2][rgb & 0xFF];
      return a | r << 16 | g << 8 | b;
    } 
    return rgb;
  }
  
  public String toString() {
    return "Colors/Equalize";
  }
}
