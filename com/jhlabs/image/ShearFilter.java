package com.jhlabs.image;

import java.awt.Rectangle;

public class ShearFilter extends TransformFilter {
  private float xangle = 0.0F;
  
  private float yangle = 0.0F;
  
  private float shx = 0.0F;
  
  private float shy = 0.0F;
  
  private float xoffset = 0.0F;
  
  private float yoffset = 0.0F;
  
  private boolean resize = true;
  
  public void setResize(boolean resize) {
    this.resize = resize;
  }
  
  public boolean isResize() {
    return this.resize;
  }
  
  public void setXAngle(float xangle) {
    this.xangle = xangle;
    initialize();
  }
  
  public float getXAngle() {
    return this.xangle;
  }
  
  public void setYAngle(float yangle) {
    this.yangle = yangle;
    initialize();
  }
  
  public float getYAngle() {
    return this.yangle;
  }
  
  private void initialize() {
    this.shx = (float)Math.sin(this.xangle);
    this.shy = (float)Math.sin(this.yangle);
  }
  
  protected void transformSpace(Rectangle r) {
    float tangent = (float)Math.tan(this.xangle);
    this.xoffset = -r.height * tangent;
    if (tangent < 0.0D)
      tangent = -tangent; 
    r.width = (int)(r.height * tangent + r.width + 0.999999F);
    tangent = (float)Math.tan(this.yangle);
    this.yoffset = -r.width * tangent;
    if (tangent < 0.0D)
      tangent = -tangent; 
    r.height = (int)(r.width * tangent + r.height + 0.999999F);
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    out[0] = x + this.xoffset + y * this.shx;
    out[1] = y + this.yoffset + x * this.shy;
  }
  
  public String toString() {
    return "Distort/Shear...";
  }
}
