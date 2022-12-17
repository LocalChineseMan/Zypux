package com.jhlabs.image;

public class BlurFilter extends ConvolveFilter {
  static final long serialVersionUID = -4753886159026796838L;
  
  protected static float[] blurMatrix = new float[] { 0.071428575F, 0.14285715F, 0.071428575F, 0.14285715F, 0.14285715F, 0.14285715F, 0.071428575F, 0.14285715F, 0.071428575F };
  
  public BlurFilter() {
    super(blurMatrix);
  }
  
  public String toString() {
    return "Blur/Simple Blur";
  }
}
