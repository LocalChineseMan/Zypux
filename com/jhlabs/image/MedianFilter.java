package com.jhlabs.image;

import java.awt.Rectangle;

public class MedianFilter extends WholeImageFilter {
  private int median(int[] array) {
    int i;
    for (i = 0; i < 4; i++) {
      int k = 0;
      int maxIndex = 0;
      for (int j = 0; j < 9; j++) {
        if (array[j] > k) {
          k = array[j];
          maxIndex = j;
        } 
      } 
      array[maxIndex] = 0;
    } 
    int max = 0;
    for (i = 0; i < 9; i++) {
      if (array[i] > max)
        max = array[i]; 
    } 
    return max;
  }
  
  private int rgbMedian(int[] r, int[] g, int[] b) {
    int index = 0, min = Integer.MAX_VALUE;
    for (int i = 0; i < 9; i++) {
      int sum = 0;
      for (int j = 0; j < 9; j++) {
        sum += Math.abs(r[i] - r[j]);
        sum += Math.abs(g[i] - g[j]);
        sum += Math.abs(b[i] - b[j]);
      } 
      if (sum < min) {
        min = sum;
        index = i;
      } 
    } 
    return index;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    int[] argb = new int[9];
    int[] r = new int[9];
    int[] g = new int[9];
    int[] b = new int[9];
    int[] outPixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int k = 0;
        for (int dy = -1; dy <= 1; dy++) {
          int iy = y + dy;
          if (0 <= iy && iy < height) {
            int ioffset = iy * width;
            for (int dx = -1; dx <= 1; dx++) {
              int ix = x + dx;
              if (0 <= ix && ix < width) {
                int rgb = inPixels[ioffset + ix];
                argb[k] = rgb;
                r[k] = rgb >> 16 & 0xFF;
                g[k] = rgb >> 8 & 0xFF;
                b[k] = rgb & 0xFF;
                k++;
              } 
            } 
          } 
        } 
        while (k < 9) {
          argb[k] = -16777216;
          b[k] = 0;
          g[k] = 0;
          r[k] = 0;
          k++;
        } 
        outPixels[index++] = argb[rgbMedian(r, g, b)];
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Blur/Median";
  }
}
