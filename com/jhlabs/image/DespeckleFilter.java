package com.jhlabs.image;

import java.awt.Rectangle;

public class DespeckleFilter extends WholeImageFilter {
  protected short pepperAndSalt(short c, short v1, short v2) {
    if (c < v1)
      c = (short)(c + 1); 
    if (c < v2)
      c = (short)(c + 1); 
    if (c > v1)
      c = (short)(c - 1); 
    if (c > v2)
      c = (short)(c - 1); 
    return c;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    short[][] r = new short[3][width];
    short[][] g = new short[3][width];
    short[][] b = new short[3][width];
    int[] outPixels = new int[width * height];
    for (int x = 0; x < width; x++) {
      int rgb = inPixels[x];
      r[1][x] = (short)(rgb >> 16 & 0xFF);
      g[1][x] = (short)(rgb >> 8 & 0xFF);
      b[1][x] = (short)(rgb & 0xFF);
    } 
    for (int y = 0; y < height; y++) {
      boolean yIn = (y > 0 && y < height - 1);
      int nextRowIndex = index + width;
      if (y < height - 1)
        for (int j = 0; j < width; j++) {
          int rgb = inPixels[nextRowIndex++];
          r[2][j] = (short)(rgb >> 16 & 0xFF);
          g[2][j] = (short)(rgb >> 8 & 0xFF);
          b[2][j] = (short)(rgb & 0xFF);
        }  
      for (int i = 0; i < width; i++) {
        boolean xIn = (i > 0 && i < width - 1);
        short or = r[1][i];
        short og = g[1][i];
        short ob = b[1][i];
        int w = i - 1;
        int e = i + 1;
        if (yIn) {
          or = pepperAndSalt(or, r[0][i], r[2][i]);
          og = pepperAndSalt(og, g[0][i], g[2][i]);
          ob = pepperAndSalt(ob, b[0][i], b[2][i]);
        } 
        if (xIn) {
          or = pepperAndSalt(or, r[1][w], r[1][e]);
          og = pepperAndSalt(og, g[1][w], g[1][e]);
          ob = pepperAndSalt(ob, b[1][w], b[1][e]);
        } 
        if (yIn && xIn) {
          or = pepperAndSalt(or, r[0][w], r[2][e]);
          og = pepperAndSalt(og, g[0][w], g[2][e]);
          ob = pepperAndSalt(ob, b[0][w], b[2][e]);
          or = pepperAndSalt(or, r[2][w], r[0][e]);
          og = pepperAndSalt(og, g[2][w], g[0][e]);
          ob = pepperAndSalt(ob, b[2][w], b[0][e]);
        } 
        outPixels[index] = inPixels[index] & 0xFF000000 | or << 16 | og << 8 | ob;
        index++;
      } 
      short[] t = r[0];
      r[0] = r[1];
      r[1] = r[2];
      r[2] = t;
      t = g[0];
      g[0] = g[1];
      g[1] = g[2];
      g[2] = t;
      t = b[0];
      b[0] = b[1];
      b[1] = b[2];
      b[2] = t;
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Blur/Despeckle...";
  }
}
