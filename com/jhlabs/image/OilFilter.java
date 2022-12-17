package com.jhlabs.image;

import java.awt.Rectangle;

public class OilFilter extends WholeImageFilter {
  static final long serialVersionUID = 1722613531684653826L;
  
  private int range = 3;
  
  private int levels = 256;
  
  public void setRange(int range) {
    this.range = range;
  }
  
  public int getRange() {
    return this.range;
  }
  
  public void setLevels(int levels) {
    this.levels = levels;
  }
  
  public int getLevels() {
    return this.levels;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    int[] rHistogram = new int[this.levels];
    int[] gHistogram = new int[this.levels];
    int[] bHistogram = new int[this.levels];
    int[] rTotal = new int[this.levels];
    int[] gTotal = new int[this.levels];
    int[] bTotal = new int[this.levels];
    int[] outPixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int i = 0; i < this.levels; i++) {
          bTotal[i] = 0;
          gTotal[i] = 0;
          rTotal[i] = 0;
          bHistogram[i] = 0;
          gHistogram[i] = 0;
          rHistogram[i] = 0;
        } 
        for (int row = -this.range; row <= this.range; row++) {
          int iy = y + row;
          if (0 <= iy && iy < height) {
            int ioffset = iy * width;
            for (int col = -this.range; col <= this.range; col++) {
              int ix = x + col;
              if (0 <= ix && ix < width) {
                int rgb = inPixels[ioffset + ix];
                int k = rgb >> 16 & 0xFF;
                int m = rgb >> 8 & 0xFF;
                int n = rgb & 0xFF;
                int ri = k * this.levels / 256;
                int gi = m * this.levels / 256;
                int bi = n * this.levels / 256;
                rTotal[ri] = rTotal[ri] + k;
                gTotal[gi] = gTotal[gi] + m;
                bTotal[bi] = bTotal[bi] + n;
                rHistogram[ri] = rHistogram[ri] + 1;
                gHistogram[gi] = gHistogram[gi] + 1;
                bHistogram[bi] = bHistogram[bi] + 1;
              } 
            } 
          } 
        } 
        int r = 0, g = 0, b = 0;
        for (int j = 1; j < this.levels; j++) {
          if (rHistogram[j] > rHistogram[r])
            r = j; 
          if (gHistogram[j] > gHistogram[g])
            g = j; 
          if (bHistogram[j] > bHistogram[b])
            b = j; 
        } 
        r = rTotal[r] / rHistogram[r];
        g = gTotal[g] / gHistogram[g];
        b = bTotal[b] / bHistogram[b];
        outPixels[index++] = 0xFF000000 | r << 16 | g << 8 | b;
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Stylize/Oil...";
  }
}
