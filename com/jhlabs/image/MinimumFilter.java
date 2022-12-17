package com.jhlabs.image;

import java.awt.Rectangle;

public class MinimumFilter extends WholeImageFilter {
  static final long serialVersionUID = 1925266438370819998L;
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    int[] outPixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = -1;
        for (int dy = -1; dy <= 1; dy++) {
          int iy = y + dy;
          if (0 <= iy && iy < height) {
            int ioffset = iy * width;
            for (int dx = -1; dx <= 1; dx++) {
              int ix = x + dx;
              if (0 <= ix && ix < width)
                pixel = PixelUtils.combinePixels(pixel, inPixels[ioffset + ix], 2); 
            } 
          } 
        } 
        outPixels[index++] = pixel;
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Blur/Minimum";
  }
}
