package com.jhlabs.math;

import java.util.Random;

public class CellularFunction2D implements Function2D {
  public float distancePower = 2.0F;
  
  public boolean cells = false;
  
  public boolean angular = false;
  
  private float[] coefficients = new float[] { 1.0F, 0.0F, 0.0F, 0.0F };
  
  private Random random = new Random();
  
  private Point[] results = null;
  
  public CellularFunction2D() {
    this.results = new Point[2];
    for (int j = 0; j < this.results.length; j++)
      this.results[j] = new Point(this); 
  }
  
  public void setCoefficient(int c, float v) {
    this.coefficients[c] = v;
  }
  
  public float getCoefficient(int c) {
    return this.coefficients[c];
  }
  
  class Point {
    int index;
    
    float x;
    
    float y;
    
    float distance;
    
    private final CellularFunction2D this$0;
    
    Point(CellularFunction2D this$0) {
      this.this$0 = this$0;
    }
  }
  
  private float checkCube(float x, float y, int cubeX, int cubeY, Point[] results) {
    this.random.setSeed((571 * cubeX + 23 * cubeY));
    int numPoints = 3 + this.random.nextInt() % 4;
    numPoints = 4;
    for (int i = 0; i < numPoints; i++) {
      float d, px = this.random.nextFloat();
      float py = this.random.nextFloat();
      float dx = Math.abs(x - px);
      float dy = Math.abs(y - py);
      if (this.distancePower == 1.0F) {
        d = dx + dy;
      } else if (this.distancePower == 2.0F) {
        d = (float)Math.sqrt((dx * dx + dy * dy));
      } else {
        d = (float)Math.pow(Math.pow(dx, this.distancePower) + Math.pow(dy, this.distancePower), (1.0F / this.distancePower));
      } 
      for (int j = 0; j < results.length; j++) {
        if ((results[j]).distance == Double.POSITIVE_INFINITY) {
          Point last = results[j];
          last.distance = d;
          last.x = px;
          last.y = py;
          results[j] = last;
          break;
        } 
        if (d < (results[j]).distance) {
          Point last = results[results.length - 1];
          for (int k = results.length - 1; k > j; k--)
            results[k] = results[k - 1]; 
          last.distance = d;
          last.x = px;
          last.y = py;
          results[j] = last;
          break;
        } 
      } 
    } 
    return (results[1]).distance;
  }
  
  public float evaluate(float x, float y) {
    for (int j = 0; j < this.results.length; j++)
      (this.results[j]).distance = Float.POSITIVE_INFINITY; 
    int ix = (int)x;
    int iy = (int)y;
    float fx = x - ix;
    float fy = y - iy;
    float d = checkCube(fx, fy, ix, iy, this.results);
    if (d > fy)
      d = checkCube(fx, fy + 1.0F, ix, iy - 1, this.results); 
    if (d > 1.0F - fy)
      d = checkCube(fx, fy - 1.0F, ix, iy + 1, this.results); 
    if (d > fx) {
      checkCube(fx + 1.0F, fy, ix - 1, iy, this.results);
      if (d > fy)
        d = checkCube(fx + 1.0F, fy + 1.0F, ix - 1, iy - 1, this.results); 
      if (d > 1.0F - fy)
        d = checkCube(fx + 1.0F, fy - 1.0F, ix - 1, iy + 1, this.results); 
    } 
    if (d > 1.0F - fx) {
      d = checkCube(fx - 1.0F, fy, ix + 1, iy, this.results);
      if (d > fy)
        d = checkCube(fx - 1.0F, fy + 1.0F, ix + 1, iy - 1, this.results); 
      if (d > 1.0F - fy)
        d = checkCube(fx - 1.0F, fy - 1.0F, ix + 1, iy + 1, this.results); 
    } 
    float t = 0.0F;
    for (int i = 0; i < 2; i++)
      t += this.coefficients[i] * (this.results[i]).distance; 
    if (this.angular)
      t = (float)(t + Math.atan2((fy - (this.results[0]).y), (fx - (this.results[0]).x)) / 6.283185307179586D + 0.5D); 
    return t;
  }
}
