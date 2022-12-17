package com.jhlabs.image;

import java.io.Serializable;

public class ThresholdFilter extends PointFilter implements Serializable {
  static final long serialVersionUID = -1899610620205446828L;
  
  private int lowerThreshold;
  
  private int lowerThreshold3;
  
  private int upperThreshold;
  
  private int upperThreshold3;
  
  private int white = 16777215;
  
  private int black = 0;
  
  public ThresholdFilter() {
    this(127);
  }
  
  public ThresholdFilter(int t) {
    setLowerThreshold(t);
    setUpperThreshold(t);
  }
  
  public void setLowerThreshold(int lowerThreshold) {
    this.lowerThreshold = lowerThreshold;
    this.lowerThreshold3 = lowerThreshold * 3;
  }
  
  public int getLowerThreshold() {
    return this.lowerThreshold;
  }
  
  public void setUpperThreshold(int upperThreshold) {
    this.upperThreshold = upperThreshold;
    this.upperThreshold3 = upperThreshold * 3;
  }
  
  public int getUpperThreshold() {
    return this.upperThreshold;
  }
  
  public void setWhite(int white) {
    this.white = white;
  }
  
  public int getWhite() {
    return this.white;
  }
  
  public void setBlack(int black) {
    this.black = black;
  }
  
  public int getBlack() {
    return this.black;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int a = rgb & 0xFF000000;
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    int l = r + g + b;
    if (l < this.lowerThreshold3)
      return a | this.black; 
    if (l > this.upperThreshold3)
      return a | this.white; 
    return rgb;
  }
  
  public String toString() {
    return "Stylize/Threshold...";
  }
}
