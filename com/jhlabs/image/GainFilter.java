package com.jhlabs.image;

public class GainFilter extends TransferFilter {
  private float gain = 0.5F;
  
  private float bias = 0.5F;
  
  protected float transferFunction(float f) {
    f = ImageMath.gain(f, this.gain);
    f = ImageMath.bias(f, this.bias);
    return f;
  }
  
  public void setGain(float gain) {
    this.gain = gain;
    this.initialized = false;
  }
  
  public float getGain() {
    return this.gain;
  }
  
  public void setBias(float bias) {
    this.bias = bias;
    this.initialized = false;
  }
  
  public float getBias() {
    return this.bias;
  }
  
  public String toString() {
    return "Colors/Gain...";
  }
}
