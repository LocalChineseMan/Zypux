package com.jhlabs.image;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class PlasmaFilter extends WholeImageFilter implements Serializable {
  static final long serialVersionUID = 6491871753122667752L;
  
  public float turbulence = 1.0F;
  
  private float scaling = 0.0F;
  
  private Colormap colormap = new LinearColormap();
  
  private Random randomGenerator;
  
  private long seed = 567L;
  
  private boolean useColormap = false;
  
  private boolean useImageColors = false;
  
  public PlasmaFilter() {
    this.randomGenerator = new Random();
  }
  
  public void setTurbulence(float turbulence) {
    this.turbulence = turbulence;
  }
  
  public float getTurbulence() {
    return this.turbulence;
  }
  
  public void setScaling(float scaling) {
    this.scaling = scaling;
  }
  
  public float getScaling() {
    return this.scaling;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public void setUseColormap(boolean useColormap) {
    this.useColormap = useColormap;
  }
  
  public boolean getUseColormap() {
    return this.useColormap;
  }
  
  public void setUseImageColors(boolean useImageColors) {
    this.useImageColors = useImageColors;
  }
  
  public boolean getUseImageColors() {
    return this.useImageColors;
  }
  
  public void setSeed(int seed) {
    this.seed = seed;
  }
  
  public int getSeed() {
    return (int)this.seed;
  }
  
  public void randomize() {
    this.seed = (new Date()).getTime();
  }
  
  private int randomRGB(int[] inPixels, int x, int y) {
    if (this.useImageColors)
      return inPixels[y * this.originalSpace.width + x]; 
    int r = (int)(255.0F * this.randomGenerator.nextFloat());
    int g = (int)(255.0F * this.randomGenerator.nextFloat());
    int b = (int)(255.0F * this.randomGenerator.nextFloat());
    return 0xFF000000 | r << 16 | g << 8 | b;
  }
  
  private int displace(int rgb, float amount) {
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    r = PixelUtils.clamp(r + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
    g = PixelUtils.clamp(g + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
    b = PixelUtils.clamp(b + (int)(amount * (this.randomGenerator.nextFloat() - 0.5D)));
    return 0xFF000000 | r << 16 | g << 8 | b;
  }
  
  private int average(int rgb1, int rgb2) {
    return PixelUtils.combinePixels(rgb1, rgb2, 13);
  }
  
  private int getPixel(int x, int y, int[] pixels, int stride) {
    return pixels[y * stride + x];
  }
  
  private void putPixel(int x, int y, int rgb, int[] pixels, int stride) {
    pixels[y * stride + x] = rgb;
  }
  
  private boolean doPixel(int x1, int y1, int x2, int y2, int[] pixels, int stride, int depth, int scale) {
    if (depth == 0) {
      int tl = getPixel(x1, y1, pixels, stride);
      int bl = getPixel(x1, y2, pixels, stride);
      int tr = getPixel(x2, y1, pixels, stride);
      int br = getPixel(x2, y2, pixels, stride);
      float amount = 256.0F / 2.0F * scale * this.turbulence;
      int i = (x1 + x2) / 2;
      int j = (y1 + y2) / 2;
      if (i == x1 && i == x2 && j == y1 && j == y2)
        return true; 
      if (i != x1 || i != x2) {
        int ml = average(tl, bl);
        ml = displace(ml, amount);
        putPixel(x1, j, ml, pixels, stride);
        if (x1 != x2) {
          int mr = average(tr, br);
          mr = displace(mr, amount);
          putPixel(x2, j, mr, pixels, stride);
        } 
      } 
      if (j != y1 || j != y2) {
        if (x1 != i || j != y2) {
          int mb = average(bl, br);
          mb = displace(mb, amount);
          putPixel(i, y2, mb, pixels, stride);
        } 
        if (y1 != y2) {
          int mt = average(tl, tr);
          mt = displace(mt, amount);
          putPixel(i, y1, mt, pixels, stride);
        } 
      } 
      if (y1 != y2 || x1 != x2) {
        int mm = average(tl, br);
        int t = average(bl, tr);
        mm = average(mm, t);
        mm = displace(mm, amount);
        putPixel(i, j, mm, pixels, stride);
      } 
      if (x2 - x1 < 3 && y2 - y1 < 3)
        return false; 
      return true;
    } 
    int mx = (x1 + x2) / 2;
    int my = (y1 + y2) / 2;
    doPixel(x1, y1, mx, my, pixels, stride, depth - 1, scale + 1);
    doPixel(x1, my, mx, y2, pixels, stride, depth - 1, scale + 1);
    doPixel(mx, y1, x2, my, pixels, stride, depth - 1, scale + 1);
    return doPixel(mx, my, x2, y2, pixels, stride, depth - 1, scale + 1);
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int[] outPixels = new int[width * height];
    this.randomGenerator.setSeed(this.seed);
    int w1 = width - 1;
    int h1 = height - 1;
    putPixel(0, 0, randomRGB(inPixels, 0, 0), outPixels, width);
    putPixel(w1, 0, randomRGB(inPixels, w1, 0), outPixels, width);
    putPixel(0, h1, randomRGB(inPixels, 0, h1), outPixels, width);
    putPixel(w1, h1, randomRGB(inPixels, w1, h1), outPixels, width);
    putPixel(w1 / 2, h1 / 2, randomRGB(inPixels, w1 / 2, h1 / 2), outPixels, width);
    putPixel(0, h1 / 2, randomRGB(inPixels, 0, h1 / 2), outPixels, width);
    putPixel(w1, h1 / 2, randomRGB(inPixels, w1, h1 / 2), outPixels, width);
    putPixel(w1 / 2, 0, randomRGB(inPixels, w1 / 2, 0), outPixels, width);
    putPixel(w1 / 2, h1, randomRGB(inPixels, w1 / 2, h1), outPixels, width);
    int depth = 1;
    while (doPixel(0, 0, width - 1, height - 1, outPixels, width, depth, 0))
      depth++; 
    if (this.useColormap && this.colormap != null) {
      int index = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          outPixels[index] = this.colormap.getColor((outPixels[index] & 0xFF) / 255.0F);
          index++;
        } 
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Texture/Plasma...";
  }
}
