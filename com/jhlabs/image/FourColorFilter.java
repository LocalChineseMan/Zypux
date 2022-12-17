package com.jhlabs.image;

public class FourColorFilter extends PointFilter {
  private int width;
  
  private int height;
  
  private int colorNW;
  
  private int colorNE;
  
  private int colorSW;
  
  private int colorSE;
  
  private int rNW;
  
  private int gNW;
  
  private int bNW;
  
  private int rNE;
  
  private int gNE;
  
  private int bNE;
  
  private int rSW;
  
  private int gSW;
  
  private int bSW;
  
  private int rSE;
  
  private int gSE;
  
  private int bSE;
  
  public FourColorFilter() {
    setColorNW(-65536);
    setColorNE(-65281);
    setColorSW(-16776961);
    setColorSE(-16711681);
  }
  
  public void setColorNW(int color) {
    this.colorNW = color;
    this.rNW = color >> 16 & 0xFF;
    this.gNW = color >> 8 & 0xFF;
    this.bNW = color & 0xFF;
  }
  
  public int getColorNW() {
    return this.colorNW;
  }
  
  public void setColorNE(int color) {
    this.colorNE = color;
    this.rNE = color >> 16 & 0xFF;
    this.gNE = color >> 8 & 0xFF;
    this.bNE = color & 0xFF;
  }
  
  public int getColorNE() {
    return this.colorNE;
  }
  
  public void setColorSW(int color) {
    this.colorSW = color;
    this.rSW = color >> 16 & 0xFF;
    this.gSW = color >> 8 & 0xFF;
    this.bSW = color & 0xFF;
  }
  
  public int getColorSW() {
    return this.colorSW;
  }
  
  public void setColorSE(int color) {
    this.colorSE = color;
    this.rSE = color >> 16 & 0xFF;
    this.gSE = color >> 8 & 0xFF;
    this.bSE = color & 0xFF;
  }
  
  public int getColorSE() {
    return this.colorSE;
  }
  
  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
    super.setDimensions(width, height);
  }
  
  public int filterRGB(int x, int y, int rgb) {
    float fx = x / this.width;
    float fy = y / this.height;
    float p = this.rNW + (this.rNE - this.rNW) * fx;
    float q = this.rSW + (this.rSE - this.rSW) * fx;
    int r = (int)(p + (q - p) * fy + 0.5F);
    p = this.gNW + (this.gNE - this.gNW) * fx;
    q = this.gSW + (this.gSE - this.gSW) * fx;
    int g = (int)(p + (q - p) * fy + 0.5F);
    p = this.bNW + (this.bNE - this.bNW) * fx;
    q = this.bSW + (this.bSE - this.bSW) * fx;
    int b = (int)(p + (q - p) * fy + 0.5F);
    return 0xFF000000 | r << 16 | g << 8 | b;
  }
  
  public String toString() {
    return "Texture/Four Color Fill...";
  }
}
