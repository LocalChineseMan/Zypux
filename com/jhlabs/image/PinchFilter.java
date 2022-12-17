package com.jhlabs.image;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class PinchFilter extends TransformFilter {
  private float angle = 0.0F;
  
  private float centreX = 0.5F;
  
  private float centreY = 0.5F;
  
  private float radius = 100.0F;
  
  private float amount = 0.5F;
  
  private float radius2 = 0.0F;
  
  private float icentreX;
  
  private float icentreY;
  
  private float width;
  
  private float height;
  
  public void setAngle(float angle) {
    this.angle = angle;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setCentreX(float centreX) {
    this.centreX = centreX;
  }
  
  public float getCentreX() {
    return this.centreX;
  }
  
  public void setCentreY(float centreY) {
    this.centreY = centreY;
  }
  
  public float getCentreY() {
    return this.centreY;
  }
  
  public void setCentre(Point2D centre) {
    this.centreX = (float)centre.getX();
    this.centreY = (float)centre.getY();
  }
  
  public Point2D getCentre() {
    return new Point2D.Float(this.centreX, this.centreY);
  }
  
  public void setRadius(float radius) {
    this.radius = radius;
  }
  
  public float getRadius() {
    return this.radius;
  }
  
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  public float getAmount() {
    return this.amount;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    this.width = src.getWidth();
    this.height = src.getHeight();
    this.icentreX = this.width * this.centreX;
    this.icentreY = this.height * this.centreY;
    if (this.radius == 0.0F)
      this.radius = Math.min(this.icentreX, this.icentreY); 
    this.radius2 = this.radius * this.radius;
    return super.filter(src, dst);
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    float dx = x - this.icentreX;
    float dy = y - this.icentreY;
    float distance = dx * dx + dy * dy;
    if (distance > this.radius2 || distance == 0.0F) {
      out[0] = x;
      out[1] = y;
    } else {
      float d = (float)Math.sqrt((distance / this.radius2));
      float t = (float)Math.pow(Math.sin(1.5707963267948966D * d), -this.amount);
      dx *= t;
      dy *= t;
      float e = 1.0F - d;
      float a = this.angle * e * e;
      float s = (float)Math.sin(a);
      float c = (float)Math.cos(a);
      out[0] = this.icentreX + c * dx - s * dy;
      out[1] = this.icentreY + s * dx + c * dy;
    } 
  }
  
  public String toString() {
    return "Distort/Pinch...";
  }
}
