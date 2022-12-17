package com.jhlabs.image;

import java.awt.Rectangle;

public class EdgeFilter extends WholeImageFilter {
  static final long serialVersionUID = -1084121755410916989L;
  
  public static final float R2 = (float)Math.sqrt(2.0D);
  
  public static final float[] ROBERTS_V = new float[] { 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F };
  
  public static final float[] ROBERTS_H = new float[] { -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F };
  
  public static final float[] PREWITT_V = new float[] { -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F };
  
  public static final float[] PREWITT_H = new float[] { -1.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
  
  public static final float[] SOBEL_V = new float[] { -1.0F, 0.0F, 1.0F, -2.0F, 0.0F, 2.0F, -1.0F, 0.0F, 1.0F };
  
  public static float[] SOBEL_H = new float[] { -1.0F, -2.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F };
  
  public static final float[] FREI_CHEN_V = new float[] { -1.0F, 0.0F, 1.0F, -R2, 0.0F, R2, -1.0F, 0.0F, 1.0F };
  
  public static float[] FREI_CHEN_H = new float[] { -1.0F, -R2, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, R2, 1.0F };
  
  protected float[] vEdgeMatrix = SOBEL_V;
  
  protected float[] hEdgeMatrix = SOBEL_H;
  
  public void setVEdgeMatrix(float[] vEdgeMatrix) {
    this.vEdgeMatrix = vEdgeMatrix;
  }
  
  public float[] getVEdgeMatrix() {
    return this.vEdgeMatrix;
  }
  
  public void setHEdgeMatrix(float[] hEdgeMatrix) {
    this.hEdgeMatrix = hEdgeMatrix;
  }
  
  public float[] getHEdgeMatrix() {
    return this.hEdgeMatrix;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    int[] outPixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = 0, g = 0, b = 0;
        int rh = 0, gh = 0, bh = 0;
        int rv = 0, gv = 0, bv = 0;
        int a = inPixels[y * width + x] & 0xFF000000;
        for (int row = -1; row <= 1; row++) {
          int ioffset, iy = y + row;
          if (0 <= iy && iy < height) {
            ioffset = iy * width;
          } else {
            ioffset = y * width;
          } 
          int moffset = 3 * (row + 1) + 1;
          for (int col = -1; col <= 1; col++) {
            int ix = x + col;
            if (0 > ix || ix >= width)
              ix = x; 
            int rgb = inPixels[ioffset + ix];
            float h = this.hEdgeMatrix[moffset + col];
            float v = this.vEdgeMatrix[moffset + col];
            r = (rgb & 0xFF0000) >> 16;
            g = (rgb & 0xFF00) >> 8;
            b = rgb & 0xFF;
            rh += (int)(h * r);
            gh += (int)(h * g);
            bh += (int)(h * b);
            rv += (int)(v * r);
            gv += (int)(v * g);
            bv += (int)(v * b);
          } 
        } 
        r = (int)(Math.sqrt((rh * rh + rv * rv)) / 1.8D);
        g = (int)(Math.sqrt((gh * gh + gv * gv)) / 1.8D);
        b = (int)(Math.sqrt((bh * bh + bv * bv)) / 1.8D);
        r = PixelUtils.clamp(r);
        g = PixelUtils.clamp(g);
        b = PixelUtils.clamp(b);
        outPixels[index++] = a | r << 16 | g << 8 | b;
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Blur/Detect Edges";
  }
}
