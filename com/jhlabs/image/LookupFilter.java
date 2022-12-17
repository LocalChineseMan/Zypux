package com.jhlabs.image;

public class LookupFilter extends PointFilter {
  private Colormap colormap = new Gradient();
  
  public LookupFilter() {
    this.canFilterIndexColorModel = true;
  }
  
  public LookupFilter(Colormap colormap) {
    this.canFilterIndexColorModel = true;
    this.colormap = colormap;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    rgb = (r + g + b) / 3;
    return this.colormap.getColor(rgb / 255.0F);
  }
  
  public String toString() {
    return "Colors/Lookup...";
  }
}
