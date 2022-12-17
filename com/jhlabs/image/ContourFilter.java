package com.jhlabs.image;

import java.awt.Rectangle;

public class ContourFilter extends WholeImageFilter {
  private float levels = 5.0F;
  
  private float scale = 1.0F;
  
  private float offset = 0.0F;
  
  private int contourColor = -16777216;
  
  public void setLevels(float levels) {
    this.levels = levels;
  }
  
  public float getLevels() {
    return this.levels;
  }
  
  public void setScale(float scale) {
    this.scale = scale;
  }
  
  public float getScale() {
    return this.scale;
  }
  
  public void setOffset(float offset) {
    this.offset = offset;
  }
  
  public float getOffset() {
    return this.offset;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    short[][] r = new short[3][width];
    int[] outPixels = new int[width * height];
    short[] table = new short[256];
    int offsetl = (int)(this.offset * 256.0F / this.levels);
    for (int i = 0; i < 256; i++)
      table[i] = (short)PixelUtils.clamp((int)(255.0D * Math.floor((this.levels * (i + offsetl) / 256.0F)) / (this.levels - 1.0F) - offsetl)); 
    for (int x = 0; x < width; x++) {
      int rgb = inPixels[x];
      r[1][x] = (short)PixelUtils.brightness(rgb);
    } 
    for (int y = 0; y < height; y++) {
      boolean yIn = (y > 0 && y < height - 1);
      int nextRowIndex = index + width;
      if (y < height - 1)
        for (int k = 0; k < width; k++) {
          int rgb = inPixels[nextRowIndex++];
          r[2][k] = (short)PixelUtils.brightness(rgb);
        }  
      for (int j = 0; j < width; j++) {
        boolean xIn = (j > 0 && j < width - 1);
        int w = j - 1;
        int e = j + 1;
        int v = 0;
        if (yIn && xIn) {
          short nwb = r[0][w];
          short neb = r[0][j];
          short swb = r[1][w];
          short seb = r[1][j];
          short nw = table[nwb];
          short ne = table[neb];
          short sw = table[swb];
          short se = table[seb];
          if (nw != ne || nw != sw || ne != se || sw != se) {
            v = (int)(this.scale * (Math.abs(nwb - neb) + Math.abs(nwb - swb) + Math.abs(neb - seb) + Math.abs(swb - seb)));
            if (v > 255)
              v = 255; 
          } 
        } 
        if (v != 0) {
          outPixels[index] = PixelUtils.combinePixels(inPixels[index], this.contourColor, 1, v);
        } else {
          outPixels[index] = inPixels[index];
        } 
        index++;
      } 
      short[] t = r[0];
      r[0] = r[1];
      r[1] = r[2];
      r[2] = t;
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Stylize/Contour...";
  }
}
