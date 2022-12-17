package com.jhlabs.image;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class KaleidoscopeFilter extends TransformFilter {
  private float angle = 0.0F;
  
  private float angle2 = 0.0F;
  
  private float centreX = 0.5F;
  
  private float centreY = 0.5F;
  
  private int sides = 3;
  
  private float radius = 0.0F;
  
  private float icentreX;
  
  private float icentreY;
  
  public KaleidoscopeFilter() {
    setEdgeAction(1);
  }
  
  public void setSides(int sides) {
    this.sides = sides;
  }
  
  public int getSides() {
    return this.sides;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setAngle2(float angle2) {
    this.angle2 = angle2;
  }
  
  public float getAngle2() {
    return this.angle2;
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
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    this.icentreX = src.getWidth() * this.centreX;
    this.icentreY = src.getHeight() * this.centreY;
    return super.filter(src, dst);
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    double dx = (x - this.icentreX);
    double dy = (y - this.icentreY);
    double r = Math.sqrt(dx * dx + dy * dy);
    double theta = Math.atan2(dy, dx) - this.angle - this.angle2;
    theta = ImageMath.triangle((float)(theta / Math.PI * this.sides * 0.5D));
    if (this.radius != 0.0F) {
      double c = Math.cos(theta);
      double radiusc = this.radius / c;
      r = radiusc * ImageMath.triangle((float)(r / radiusc));
    } 
    theta += this.angle;
    out[0] = (float)(this.icentreX + r * Math.cos(theta));
    out[1] = (float)(this.icentreY + r * Math.sin(theta));
  }
  
  public String toString() {
    return "Distort/Kaleidoscope...";
  }
}
