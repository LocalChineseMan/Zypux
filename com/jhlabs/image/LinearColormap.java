package com.jhlabs.image;

import java.io.Serializable;

public class LinearColormap implements Colormap, Serializable {
  static final long serialVersionUID = 4256182891287368612L;
  
  private int color1;
  
  private int color2;
  
  public LinearColormap() {
    this(-16777216, -1);
  }
  
  public LinearColormap(int color1, int color2) {
    this.color1 = color1;
    this.color2 = color2;
  }
  
  public void setColor1(int color1) {
    this.color1 = color1;
  }
  
  public int getColor1() {
    return this.color1;
  }
  
  public void setColor2(int color2) {
    this.color2 = color2;
  }
  
  public int getColor2() {
    return this.color2;
  }
  
  public int getColor(float v) {
    return ImageMath.mixColors(ImageMath.clamp(v, 0.0F, 1.0F), this.color1, this.color2);
  }
}
