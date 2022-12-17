package com.jhlabs.image;

import java.awt.Rectangle;
import java.io.Serializable;

public class QuantizeFilter extends WholeImageFilter implements Serializable {
  protected static final int[] matrix = new int[] { 0, 0, 0, 0, 0, 7, 3, 5, 1 };
  
  private int sum = 16;
  
  private boolean dither;
  
  private int numColors = 256;
  
  private boolean serpentine = true;
  
  public void setNumColors(int numColors) {
    this.numColors = Math.min(Math.max(numColors, 8), 256);
  }
  
  public int getNumColors() {
    return this.numColors;
  }
  
  public void setDither(boolean dither) {
    this.dither = dither;
  }
  
  public boolean getDither() {
    return this.dither;
  }
  
  public void setSerpentine(boolean serpentine) {
    this.serpentine = serpentine;
  }
  
  public boolean getSerpentine() {
    return this.serpentine;
  }
  
  public void quantize(int[] inPixels, int[] outPixels, int width, int height, int numColors, boolean dither, boolean serpentine) {
    int count = width * height;
    Quantizer quantizer = new OctTreeQuantizer();
    quantizer.setup(numColors);
    quantizer.addPixels(inPixels, 0, count);
    int[] table = quantizer.buildColorTable();
    if (!dither) {
      for (int i = 0; i < count; i++)
        outPixels[i] = table[quantizer.getIndexForColor(inPixels[i])]; 
    } else {
      int index = 0;
      for (int y = 0; y < height; y++) {
        int direction;
        boolean reverse = (serpentine && (y & 0x1) == 1);
        if (reverse) {
          index = y * width + width - 1;
          direction = -1;
        } else {
          index = y * width;
          direction = 1;
        } 
        for (int x = 0; x < width; x++) {
          int rgb1 = inPixels[index];
          int rgb2 = table[quantizer.getIndexForColor(rgb1)];
          outPixels[index] = rgb2;
          int r1 = rgb1 >> 16 & 0xFF;
          int g1 = rgb1 >> 8 & 0xFF;
          int b1 = rgb1 & 0xFF;
          int r2 = rgb2 >> 16 & 0xFF;
          int g2 = rgb2 >> 8 & 0xFF;
          int b2 = rgb2 & 0xFF;
          int er = r1 - r2;
          int eg = g1 - g2;
          int eb = b1 - b2;
          for (int i = -1; i <= 1; i++) {
            int iy = i + y;
            if (0 <= iy && iy < height)
              for (int j = -1; j <= 1; j++) {
                int jx = j + x;
                if (0 <= jx && jx < width) {
                  int w;
                  if (reverse) {
                    w = matrix[(i + 1) * 3 - j + 1];
                  } else {
                    w = matrix[(i + 1) * 3 + j + 1];
                  } 
                  if (w != 0) {
                    int k = reverse ? (index - j) : (index + j);
                    rgb1 = inPixels[k];
                    r1 = rgb1 >> 16 & 0xFF;
                    g1 = rgb1 >> 8 & 0xFF;
                    b1 = rgb1 & 0xFF;
                    r1 += er * w / this.sum;
                    g1 += eg * w / this.sum;
                    b1 += eb * w / this.sum;
                    inPixels[k] = PixelUtils.clamp(r1) << 16 | PixelUtils.clamp(g1) << 8 | PixelUtils.clamp(b1);
                  } 
                } 
              }  
          } 
          index += direction;
        } 
      } 
    } 
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int[] outPixels = new int[width * height];
    quantize(inPixels, outPixels, width, height, this.numColors, this.dither, this.serpentine);
    return outPixels;
  }
  
  public String toString() {
    return "Colors/Quantize...";
  }
}
