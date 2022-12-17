package com.jhlabs.image;

public class GammaFilter extends TransferFilter {
  private float rGamma;
  
  private float gGamma;
  
  private float bGamma;
  
  public GammaFilter() {
    this(1.0F);
  }
  
  public GammaFilter(float gamma) {
    this(gamma, gamma, gamma);
  }
  
  public GammaFilter(float rGamma, float gGamma, float bGamma) {
    setGamma(rGamma, gGamma, bGamma);
  }
  
  public void setGamma(float rGamma, float gGamma, float bGamma) {
    this.rGamma = rGamma;
    this.gGamma = gGamma;
    this.bGamma = bGamma;
    this.initialized = false;
  }
  
  public void setGamma(float gamma) {
    setGamma(gamma, gamma, gamma);
  }
  
  public float getGamma() {
    return this.rGamma;
  }
  
  protected void initialize() {
    this.rTable = makeTable(this.rGamma);
    if (this.gGamma == this.rGamma) {
      this.gTable = this.rTable;
    } else {
      this.gTable = makeTable(this.gGamma);
    } 
    if (this.bGamma == this.rGamma) {
      this.bTable = this.rTable;
    } else if (this.bGamma == this.gGamma) {
      this.bTable = this.gTable;
    } else {
      this.bTable = makeTable(this.bGamma);
    } 
  }
  
  protected int[] makeTable(float gamma) {
    int[] table = new int[256];
    for (int i = 0; i < 256; i++) {
      int v = (int)(255.0D * Math.pow(i / 255.0D, 1.0D / gamma) + 0.5D);
      if (v > 255)
        v = 255; 
      table[i] = v;
    } 
    return table;
  }
  
  public String toString() {
    return "Colors/Gamma...";
  }
}
