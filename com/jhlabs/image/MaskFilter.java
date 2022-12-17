package com.jhlabs.image;

import java.io.Serializable;

public class MaskFilter extends PointFilter implements Serializable {
  private int mask;
  
  public MaskFilter() {
    this(-16711681);
  }
  
  public MaskFilter(int mask) {
    this.canFilterIndexColorModel = true;
    setMask(mask);
  }
  
  public void setMask(int mask) {
    this.mask = mask;
  }
  
  public int getMask() {
    return this.mask;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    return rgb & this.mask;
  }
  
  public String toString() {
    return "Mask";
  }
}
