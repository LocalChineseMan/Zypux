package com.jhlabs.image;

public class ContrastFilter extends TransferFilter {
  private float brightness = 1.0F;
  
  private float contrast = 1.0F;
  
  protected float transferFunction(float f) {
    f *= this.brightness;
    f = (f - 0.5F) * this.contrast + 0.5F;
    return f;
  }
  
  public void setBrightness(float brightness) {
    this.brightness = brightness;
    this.initialized = false;
  }
  
  public float getBrightness() {
    return this.brightness;
  }
  
  public void setContrast(float contrast) {
    this.contrast = contrast;
    this.initialized = false;
  }
  
  public float getContrast() {
    return this.contrast;
  }
  
  public String toString() {
    return "Colors/Contrast...";
  }
}
