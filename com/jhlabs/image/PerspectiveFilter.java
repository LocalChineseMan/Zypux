package com.jhlabs.image;

import java.awt.Rectangle;

public class PerspectiveFilter extends TransformFilter {
  private float x0;
  
  private float y0;
  
  private float x1;
  
  private float y1;
  
  private float x2;
  
  private float y2;
  
  private float x3;
  
  private float y3;
  
  private float dx1;
  
  private float dy1;
  
  private float dx2;
  
  private float dy2;
  
  private float dx3;
  
  private float dy3;
  
  private float A;
  
  private float B;
  
  private float C;
  
  private float D;
  
  private float E;
  
  private float F;
  
  private float G;
  
  private float H;
  
  private float I;
  
  public PerspectiveFilter() {
    this(0.0F, 0.0F, 100.0F, 0.0F, 100.0F, 100.0F, 0.0F, 100.0F);
  }
  
  public PerspectiveFilter(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
    setCorners(x0, y0, x1, y1, x2, y2, x3, y3);
  }
  
  public void setCorners(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
    float a11, a12, a13, a21, a22, a23, a31, a32;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.x3 = x3;
    this.y3 = y3;
    this.dx1 = x1 - x2;
    this.dy1 = y1 - y2;
    this.dx2 = x3 - x2;
    this.dy2 = y3 - y2;
    this.dx3 = x0 - x1 + x2 - x3;
    this.dy3 = y0 - y1 + y2 - y3;
    if (this.dx3 == 0.0F && this.dy3 == 0.0F) {
      a11 = x1 - x0;
      a21 = x2 - x1;
      a31 = x0;
      a12 = y1 - y0;
      a22 = y2 - y1;
      a32 = y0;
      a13 = a23 = 0.0F;
    } else {
      a13 = (this.dx3 * this.dy2 - this.dx2 * this.dy3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
      a23 = (this.dx1 * this.dy3 - this.dy1 * this.dx3) / (this.dx1 * this.dy2 - this.dy1 * this.dx2);
      a11 = x1 - x0 + a13 * x1;
      a21 = x3 - x0 + a23 * x3;
      a31 = x0;
      a12 = y1 - y0 + a13 * y1;
      a22 = y3 - y0 + a23 * y3;
      a32 = y0;
    } 
    this.A = a22 - a32 * a23;
    this.B = a31 * a23 - a21;
    this.C = a21 * a32 - a31 * a22;
    this.D = a32 * a13 - a12;
    this.E = a11 - a31 * a13;
    this.F = a31 * a12 - a11 * a32;
    this.G = a12 * a23 - a22 * a13;
    this.H = a21 * a13 - a11 * a23;
    this.I = a11 * a22 - a21 * a12;
  }
  
  protected void transformSpace(Rectangle rect) {
    rect.x = (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
    rect.y = (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
    rect.width = (int)Math.max(Math.max(this.x0, this.x1), Math.max(this.x2, this.x3)) - rect.x;
    rect.height = (int)Math.max(Math.max(this.y0, this.y1), Math.max(this.y2, this.y3)) - rect.y;
  }
  
  public float getOriginX() {
    return this.x0 - (int)Math.min(Math.min(this.x0, this.x1), Math.min(this.x2, this.x3));
  }
  
  public float getOriginY() {
    return this.y0 - (int)Math.min(Math.min(this.y0, this.y1), Math.min(this.y2, this.y3));
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    out[0] = this.originalSpace.width * (this.A * x + this.B * y + this.C) / (this.G * x + this.H * y + this.I);
    out[1] = this.originalSpace.height * (this.D * x + this.E * y + this.F) / (this.G * x + this.H * y + this.I);
  }
  
  public String toString() {
    return "Distort/Perspective...";
  }
}
