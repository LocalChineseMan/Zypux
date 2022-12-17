package com.jhlabs.image;

public class SharpenFilter extends ConvolveFilter {
  static final long serialVersionUID = -4883137561307845895L;
  
  protected static float[] sharpenMatrix = new float[] { 0.0F, -0.2F, 0.0F, -0.2F, 1.8F, -0.2F, 0.0F, -0.2F, 0.0F };
  
  public SharpenFilter() {
    super(sharpenMatrix);
  }
  
  public String toString() {
    return "Blur/Sharpen";
  }
}
