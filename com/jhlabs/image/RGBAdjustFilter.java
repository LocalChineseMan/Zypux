package com.jhlabs.image;

import java.io.Serializable;

public class RGBAdjustFilter extends PointFilter implements Serializable {
  public float rFactor;
  
  public float gFactor;
  
  public float bFactor;
  
  public RGBAdjustFilter() {
    this(0.0F, 0.0F, 0.0F);
  }
  
  public RGBAdjustFilter(float r, float g, float b) {
    this.rFactor = 1.0F + r;
    this.gFactor = 1.0F + g;
    this.bFactor = 1.0F + b;
    this.canFilterIndexColorModel = true;
  }
  
  public void setRFactor(float rFactor) {
    this.rFactor = 1.0F + rFactor;
  }
  
  public float getRFactor() {
    return this.rFactor - 1.0F;
  }
  
  public void setGFactor(float gFactor) {
    this.gFactor = 1.0F + gFactor;
  }
  
  public float getGFactor() {
    return this.gFactor - 1.0F;
  }
  
  public void setBFactor(float bFactor) {
    this.bFactor = 1.0F + bFactor;
  }
  
  public float getBFactor() {
    return this.bFactor - 1.0F;
  }
  
  public int[] getLUT() {
    int[] lut = new int[256];
    for (int i = 0; i < 256; i++)
      lut[i] = filterRGB(0, 0, i << 24 | i << 16 | i << 8 | i); 
    return lut;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int a = rgb & 0xFF000000;
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    r = PixelUtils.clamp((int)(r * this.rFactor));
    g = PixelUtils.clamp((int)(g * this.gFactor));
    b = PixelUtils.clamp((int)(b * this.bFactor));
    return a | r << 16 | g << 8 | b;
  }
  
  public String toString() {
    return "Colors/Adjust RGB...";
  }
}
