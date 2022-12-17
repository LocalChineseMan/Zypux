package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.util.Random;

public class DissolveFilter extends PointFilter {
  private float density = 1.0F;
  
  private float softness = 0.0F;
  
  private float minDensity;
  
  private float maxDensity;
  
  private Random randomNumbers;
  
  public void setDensity(float density) {
    this.density = density;
  }
  
  public float getDensity() {
    return this.density;
  }
  
  public void setSoftness(float softness) {
    this.softness = softness;
  }
  
  public float getSoftness() {
    return this.softness;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    float d = (1.0F - this.density) * (1.0F + this.softness);
    this.minDensity = d - this.softness;
    this.maxDensity = d;
    this.randomNumbers = new Random(0L);
    return super.filter(src, dst);
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int a = rgb >> 24 & 0xFF;
    float v = this.randomNumbers.nextFloat();
    float f = ImageMath.smoothStep(this.minDensity, this.maxDensity, v);
    return (int)(a * f) << 24 | rgb & 0xFFFFFF;
  }
  
  public String toString() {
    return "Stylize/Dissolve...";
  }
}
