package com.jhlabs.image;

public class BumpFilter extends ConvolveFilter {
  static final long serialVersionUID = 2528502820741699111L;
  
  protected static float[] embossMatrix = new float[] { -1.0F, -1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F };
  
  public BumpFilter() {
    super(embossMatrix);
  }
  
  public String toString() {
    return "Blur/Emboss Edges";
  }
}
