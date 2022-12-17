package com.jhlabs.image;

public class SolarizeFilter extends TransferFilter {
  protected float transferFunction(float v) {
    return (v > 0.5F) ? (2.0F * (v - 0.5F)) : (2.0F * (0.5F - v));
  }
  
  public String toString() {
    return "Colors/Solarize";
  }
}
