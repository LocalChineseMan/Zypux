package com.jhlabs.image;

import java.awt.Rectangle;

public class LifeFilter extends BinaryFilter {
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    int[] outPixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = 0, g = 0, b = 0;
        int pixel = inPixels[y * width + x];
        int a = pixel & 0xFF000000;
        int neighbours = 0;
        for (int row = -1; row <= 1; row++) {
          int iy = y + row;
          if (0 <= iy && iy < height) {
            int ioffset = iy * width;
            for (int col = -1; col <= 1; col++) {
              int ix = x + col;
              if ((row != 0 || col != 0) && 0 <= ix && ix < width) {
                int rgb = inPixels[ioffset + ix];
                if (this.blackFunction.isBlack(rgb))
                  neighbours++; 
              } 
            } 
          } 
        } 
        if (this.blackFunction.isBlack(pixel)) {
          outPixels[index++] = (neighbours == 2 || neighbours == 3) ? pixel : -1;
        } else {
          outPixels[index++] = (neighbours == 3) ? -16777216 : pixel;
        } 
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Binary/Life";
  }
}
