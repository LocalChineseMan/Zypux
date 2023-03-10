package com.jhlabs.image;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class WaterFilter extends TransformFilter {
  static final long serialVersionUID = 8789236343162990941L;
  
  private float wavelength = 16.0F;
  
  private float amplitude = 10.0F;
  
  private float phase = 0.0F;
  
  private float centreX = 0.5F;
  
  private float centreY = 0.5F;
  
  private float radius = 50.0F;
  
  private float radius2 = 0.0F;
  
  private float icentreX;
  
  private float icentreY;
  
  public WaterFilter() {
    setEdgeAction(1);
  }
  
  public void setWavelength(float wavelength) {
    this.wavelength = wavelength;
  }
  
  public float getWavelength() {
    return this.wavelength;
  }
  
  public void setAmplitude(float amplitude) {
    this.amplitude = amplitude;
  }
  
  public float getAmplitude() {
    return this.amplitude;
  }
  
  public void setPhase(float phase) {
    this.phase = phase;
  }
  
  public float getPhase() {
    return this.phase;
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
  
  private boolean inside(int v, int a, int b) {
    return (a <= v && v <= b);
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    this.icentreX = src.getWidth() * this.centreX;
    this.icentreY = src.getHeight() * this.centreY;
    if (this.radius == 0.0F)
      this.radius = Math.min(this.icentreX, this.icentreY); 
    this.radius2 = this.radius * this.radius;
    return super.filter(src, dst);
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    float dx = x - this.icentreX;
    float dy = y - this.icentreY;
    float distance2 = dx * dx + dy * dy;
    if (distance2 > this.radius2) {
      out[0] = x;
      out[1] = y;
    } else {
      float distance = (float)Math.sqrt(distance2);
      float amount = this.amplitude * (float)Math.sin((distance / this.wavelength * 6.2831855F - this.phase));
      amount *= (this.radius - distance) / this.radius;
      if (distance != 0.0F)
        amount *= this.wavelength / distance; 
      out[0] = x + dx * amount;
      out[1] = y + dy * amount;
    } 
  }
  
  public String toString() {
    return "Distort/Water Ripples...";
  }
}
