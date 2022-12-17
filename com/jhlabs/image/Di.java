package com.jhlabs.image;

import java.awt.Rectangle;

public class DiffusionFilter extends WholeImageFilter {
  protected static final int[] diffusionMatrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
  
  private int[] matrix;
  
  private int sum = 16;
  
  private boolean serpentine = true;
  
  private boolean colorDither = true;
  
  public int levels = 6;
  
  public DiffusionFilter() {
    setMatrix(diffusionMatrix);
  }
  
  public void setSerpentine(boolean serpentine) {
    this.serpentine = serpentine;
  }
  
  public boolean getSerpentine() {
    return this.serpentine;
  }
  
  public void setColorDither(boolean colorDither) {
    this.colorDither = colorDither;
  }
  
  public boolean getColorDither() {
    return this.colorDither;
  }
  
  public void setMatrix(int[] matrix) {
    this.matrix = matrix;
    this.sum = 0;
    for (int i = 0; i < matrix.length; i++)
      this.sum += matrix[i]; 
  }
  
  public int[] getMatrix() {
    return this.matrix;
  }
  
  public void setLevels(int levels) {
    this.levels = levels;
  }
  
  public int getLevels() {
    return this.levels;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int[] outPixels = new int[width * height];
    int index = 0;
    int[] map = new int[this.levels];
    for (int i = 0; i < this.levels; i++) {
      int v = 255 * i / (this.levels - 1);
      map[i] = v;
    } 
    int[] div = new int[256];
    for (int j = 0; j < 256; j++)
      div[j] = this.levels * j / 256; 
    for (int y = 0; y < height; y++) {
      int direction;
      boolean reverse = (this.serpentine && (y & 0x1) == 1);
      if (reverse) {
        index = y * width + width - 1;
        direction = -1;
      } else {
        index = y * width;
        direction = 1;
      } 
      for (int x = 0; x < width; x++) {
        int rgb1 = inPixels[index];
        int r1 = rgb1 >> 16 & 0xFF;
        int g1 = rgb1 >> 8 & 0xFF;
        int b1 = rgb1 & 0xFF;
        if (!this.colorDither)
          r1 = g1 = b1 = (r1 + g1 + b1) / 3; 
        int r2 = map[div[r1]];
        int g2 = map[div[g1]];
        int b2 = map[div[b1]];
        outPixels[index] = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
        int er = r1 - r2;
        int eg = g1 - g2;
        int eb = b1 - b2;
        for (int k = -1; k <= 1; k++) {
          int iy = k + y;
          if (0 <= iy && iy < height)
            for (int m = -1; m <= 1; m++) {
              int jx = m + x;
              if (0 <= jx && jx < width) {
                int w;
                if (reverse) {
                  w = this.matrix[(k + 1) * 3 - m + 1];
                } else {
                  w = this.matrix[(k + 1) * 3 + m + 1];
                } 
                if (w != 0) {
                  int n = reverse ? (index - m) : (index + m);
                  rgb1 = inPixels[n];
                  r1 = rgb1 >> 16 & 0xFF;
                  g1 = rgb1 >> 8 & 0xFF;
                  b1 = rgb1 & 0xFF;
                  r1 += er * w / this.sum;
                  g1 += eg * w / this.sum;
                  b1 += eb * w / this.sum;
                  inPixels[n] = PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1);
                } 
              } 
            }  
        } 
        index += direction;
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Colors/Diffusion Dither...";
  }
}
