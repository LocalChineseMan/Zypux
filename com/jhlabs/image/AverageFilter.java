package com.jhlabs.image;

public class AverageFilter extends ConvolveFilter {
  protected static float[] theMatrix = new float[] { 0.1F, 0.1F, 0.1F, 0.1F, 0.2F, 0.1F, 0.1F, 0.1F, 0.1F };
  
  public AverageFilter() {
    super(theMatrix);
  }
  
  public String toString() {
    return "Blur/Average Blur";
  }
}
