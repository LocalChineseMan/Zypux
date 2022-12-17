package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.awt.geom.Point2D;

public class FlareFilter extends PointFilter {
  private int rays = 50;
  
  private float radius;
  
  private float baseAmount = 1.0F;
  
  private float ringAmount = 0.2F;
  
  private float rayAmount = 0.1F;
  
  private int color = -1;
  
  private int width;
  
  private int height;
  
  private float centreX = 0.5F, centreY = 0.5F;
  
  private float ringWidth = 1.6F;
  
  private float linear = 0.03F;
  
  private float gauss = 0.006F;
  
  private float mix = 0.5F;
  
  private float falloff = 6.0F;
  
  private float sigma;
  
  private float icentreX;
  
  private float icentreY;
  
  public FlareFilter() {
    setRadius(50.0F);
  }
  
  public void setColor(int color) {
    this.color = color;
  }
  
  public int getColor() {
    return this.color;
  }
  
  public void setRingWidth(float ringWidth) {
    this.ringWidth = ringWidth;
  }
  
  public float getRingWidth() {
    return this.ringWidth;
  }
  
  public void setBaseAmount(float baseAmount) {
    this.baseAmount = baseAmount;
  }
  
  public float getBaseAmount() {
    return this.baseAmount;
  }
  
  public void setRingAmount(float ringAmount) {
    this.ringAmount = ringAmount;
  }
  
  public float getRingAmount() {
    return this.ringAmount;
  }
  
  public void setRayAmount(float rayAmount) {
    this.rayAmount = rayAmount;
  }
  
  public float getRayAmount() {
    return this.rayAmount;
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
    this.sigma = radius / 3.0F;
  }
  
  public float getRadius() {
    return this.radius;
  }
  
  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
    this.icentreX = this.centreX * width;
    this.icentreY = this.centreY * height;
    super.setDimensions(width, height);
  }
  
  public int filterRGB(int x, int y, int rgb) {
    float ring, dx = x - this.icentreX;
    float dy = y - this.icentreY;
    float distance = (float)Math.sqrt((dx * dx + dy * dy));
    float a = (float)Math.exp((-distance * distance * this.gauss)) * this.mix + (float)Math.exp((-distance * this.linear)) * (1.0F - this.mix);
    a *= this.baseAmount;
    if (distance > this.radius + this.ringWidth)
      a = ImageMath.lerp((distance - this.radius + this.ringWidth) / this.falloff, a, 0.0F); 
    if (distance < this.radius - this.ringWidth || distance > this.radius + this.ringWidth) {
      ring = 0.0F;
    } else {
      ring = Math.abs(distance - this.radius) / this.ringWidth;
      ring = 1.0F - ring * ring * (3.0F - 2.0F * ring);
      ring *= this.ringAmount;
    } 
    a += ring;
    float angle = (float)Math.atan2(dx, dy) + 3.1415927F;
    angle = (ImageMath.mod(angle / 3.1415927F * 17.0F + 1.0F + Noise.noise1(angle * 10.0F), 1.0F) - 0.5F) * 2.0F;
    angle = Math.abs(angle);
    angle = (float)Math.pow(angle, 5.0D);
    float b = this.rayAmount * angle / (1.0F + distance * 0.1F);
    a += b;
    a = ImageMath.clamp(a, 0.0F, 1.0F);
    return ImageMath.mixColors(a, rgb, this.color);
  }
  
  public String toString() {
    return "Stylize/Flare...";
  }
}
