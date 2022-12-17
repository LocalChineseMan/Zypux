package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class StampFilter extends PointFilter {
  private float threshold;
  
  private float softness = 0.0F;
  
  protected float radius = 5.0F;
  
  private float lowerThreshold3;
  
  private float upperThreshold3;
  
  private int white = -1;
  
  private int black = -16777216;
  
  public StampFilter() {
    this(0.5F);
  }
  
  public StampFilter(float threshold) {
    setThreshold(threshold);
  }
  
  public void setRadius(float radius) {
    this.radius = radius;
  }
  
  public float getRadius() {
    return this.radius;
  }
  
  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }
  
  public float getThreshold() {
    return this.threshold;
  }
  
  public void setSoftness(float softness) {
    this.softness = softness;
  }
  
  public float getSoftness() {
    return this.softness;
  }
  
  public void setWhite(int white) {
    this.white = white;
  }
  
  public int getWhite() {
    return this.white;
  }
  
  public void setBlack(int black) {
    this.black = black;
  }
  
  public int getBlack() {
    return this.black;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    dst = (new GaussianFilter((int)this.radius)).filter(src, null);
    this.lowerThreshold3 = 765.0F * (this.threshold - this.softness * 0.5F);
    this.upperThreshold3 = 765.0F * (this.threshold + this.softness * 0.5F);
    return super.filter(dst, dst);
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int a = rgb & 0xFF000000;
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    int l = r + g + b;
    float f = ImageMath.smoothStep(this.lowerThreshold3, this.upperThreshold3, l);
    return ImageMath.mixColors(f, this.black, this.white);
  }
  
  public String toString() {
    return "Stylize/Stamp...";
  }
}
