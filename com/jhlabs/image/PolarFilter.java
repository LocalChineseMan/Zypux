package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class PolarFilter extends TransformFilter {
  public static final int RECT_TO_POLAR = 0;
  
  public static final int POLAR_TO_RECT = 1;
  
  public static final int INVERT_IN_CIRCLE = 2;
  
  private int type;
  
  private float width;
  
  private float height;
  
  private float centreX;
  
  private float centreY;
  
  private float radius;
  
  public PolarFilter() {
    this(0);
  }
  
  public PolarFilter(int type) {
    this.type = type;
    setEdgeAction(1);
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    this.width = src.getWidth();
    this.height = src.getHeight();
    this.centreX = this.width / 2.0F;
    this.centreY = this.height / 2.0F;
    this.radius = Math.max(this.centreY, this.centreX);
    return super.filter(src, dst);
  }
  
  public void setType(int type) {
    this.type = type;
  }
  
  public int getType() {
    return this.type;
  }
  
  private float sqr(float x) {
    return x * x;
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    float theta, t, m, theta2, nx, ny, dx, dy, distance2, r = 0.0F;
    switch (this.type) {
      case 0:
        theta = 0.0F;
        if (x >= this.centreX) {
          if (y > this.centreY) {
            theta = 3.1415927F - (float)Math.atan(((x - this.centreX) / (y - this.centreY)));
            r = (float)Math.sqrt((sqr(x - this.centreX) + sqr(y - this.centreY)));
          } else if (y < this.centreY) {
            theta = (float)Math.atan(((x - this.centreX) / (this.centreY - y)));
            r = (float)Math.sqrt((sqr(x - this.centreX) + sqr(this.centreY - y)));
          } else {
            theta = 1.5707964F;
            r = x - this.centreX;
          } 
        } else if (x < this.centreX) {
          if (y < this.centreY) {
            theta = 6.2831855F - (float)Math.atan(((this.centreX - x) / (this.centreY - y)));
            r = (float)Math.sqrt((sqr(this.centreX - x) + sqr(this.centreY - y)));
          } else if (y > this.centreY) {
            theta = 3.1415927F + (float)Math.atan(((this.centreX - x) / (y - this.centreY)));
            r = (float)Math.sqrt((sqr(this.centreX - x) + sqr(y - this.centreY)));
          } else {
            theta = 4.712389F;
            r = this.centreX - x;
          } 
        } 
        if (x != this.centreX) {
          m = Math.abs((y - this.centreY) / (x - this.centreX));
        } else {
          m = 0.0F;
        } 
        if (m <= this.height / this.width) {
          if (x == this.centreX) {
            float xmax = 0.0F;
            float ymax = this.centreY;
          } else {
            float xmax = this.centreX;
            float ymax = m * xmax;
          } 
        } else {
          float ymax = this.centreY;
          float xmax = ymax / m;
        } 
        out[0] = this.width - 1.0F - (this.width - 1.0F) / 6.2831855F * theta;
        out[1] = this.height * r / this.radius;
        break;
      case 1:
        theta = x / this.width * 6.2831855F;
        if (theta >= 4.712389F) {
          theta2 = 6.2831855F - theta;
        } else if (theta >= 3.1415927F) {
          theta2 = theta - 3.1415927F;
        } else if (theta >= 1.5707964F) {
          theta2 = 3.1415927F - theta;
        } else {
          theta2 = theta;
        } 
        t = (float)Math.tan(theta2);
        if (t != 0.0F) {
          m = 1.0F / t;
        } else {
          m = 0.0F;
        } 
        if (m <= this.height / this.width) {
          if (theta2 == 0.0F) {
            float f1 = 0.0F;
            float f2 = this.centreY;
          } else {
            float xmax = this.centreX;
            float ymax = m * xmax;
          } 
        } else {
          float ymax = this.centreY;
          float xmax = ymax / m;
        } 
        r = this.radius * y / this.height;
        nx = -r * (float)Math.sin(theta2);
        ny = r * (float)Math.cos(theta2);
        if (theta >= 4.712389F) {
          out[0] = this.centreX - nx;
          out[1] = this.centreY - ny;
          break;
        } 
        if (theta >= Math.PI) {
          out[0] = this.centreX - nx;
          out[1] = this.centreY + ny;
          break;
        } 
        if (theta >= 1.5707963267948966D) {
          out[0] = this.centreX + nx;
          out[1] = this.centreY + ny;
          break;
        } 
        out[0] = this.centreX + nx;
        out[1] = this.centreY - ny;
        break;
      case 2:
        dx = x - this.centreX;
        dy = y - this.centreY;
        distance2 = dx * dx + dy * dy;
        out[0] = this.centreX + this.centreX * this.centreX * dx / distance2;
        out[1] = this.centreY + this.centreY * this.centreY * dy / distance2;
        break;
    } 
  }
  
  public String toString() {
    return "Distort/Polar Coordinates...";
  }
}
