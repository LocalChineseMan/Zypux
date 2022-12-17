package com.jhlabs.image;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class QuiltFilter extends WholeImageFilter implements Serializable {
  private Random randomGenerator;
  
  private long seed = 567L;
  
  private int iterations = 25000;
  
  private float a = -0.59F;
  
  private float b = 0.2F;
  
  private float c = 0.1F;
  
  private float d = 0.0F;
  
  private int k = 0;
  
  private Colormap colormap = new LinearColormap();
  
  public QuiltFilter() {
    this.randomGenerator = new Random();
  }
  
  public void randomize() {
    this.seed = (new Date()).getTime();
    this.randomGenerator.setSeed(this.seed);
    this.a = this.randomGenerator.nextFloat();
    this.b = this.randomGenerator.nextFloat();
    this.c = this.randomGenerator.nextFloat();
    this.d = this.randomGenerator.nextFloat();
    this.k = this.randomGenerator.nextInt() % 20 - 10;
  }
  
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
  
  public int getIterations() {
    return this.iterations;
  }
  
  public void setA(float a) {
    this.a = a;
  }
  
  public float getA() {
    return this.a;
  }
  
  public void setB(float b) {
    this.b = b;
  }
  
  public float getB() {
    return this.b;
  }
  
  public void setC(float c) {
    this.c = c;
  }
  
  public float getC() {
    return this.c;
  }
  
  public void setD(float d) {
    this.d = d;
  }
  
  public float getD() {
    return this.d;
  }
  
  public void setK(int k) {
    this.k = k;
  }
  
  public int getK() {
    return this.k;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int[] outPixels = new int[width * height];
    int i = 0;
    int max = 0;
    float x = 0.1F;
    float y = 0.3F;
    int n;
    for (n = 0; n < 20; n++) {
      float mx = 3.1415927F * x;
      float my = 3.1415927F * y;
      float smx2 = (float)Math.sin((2.0F * mx));
      float smy2 = (float)Math.sin((2.0F * my));
      float x1 = (float)((this.a * smx2) + (this.b * smx2) * Math.cos((2.0F * my)) + this.c * Math.sin((4.0F * mx)) + this.d * Math.sin((6.0F * mx)) * Math.cos((4.0F * my)) + (this.k * x));
      x1 = (x1 >= 0.0F) ? (x1 - (int)x1) : (x1 - (int)x1 + 1.0F);
      float y1 = (float)((this.a * smy2) + (this.b * smy2) * Math.cos((2.0F * mx)) + this.c * Math.sin((4.0F * my)) + this.d * Math.sin((6.0F * my)) * Math.cos((4.0F * mx)) + (this.k * y));
      y1 = (y1 >= 0.0F) ? (y1 - (int)y1) : (y1 - (int)y1 + 1.0F);
      x = x1;
      y = y1;
    } 
    for (n = 0; n < this.iterations; n++) {
      float mx = 3.1415927F * x;
      float my = 3.1415927F * y;
      float x1 = (float)(this.a * Math.sin((2.0F * mx)) + this.b * Math.sin((2.0F * mx)) * Math.cos((2.0F * my)) + this.c * Math.sin((4.0F * mx)) + this.d * Math.sin((6.0F * mx)) * Math.cos((4.0F * my)) + (this.k * x));
      x1 = (x1 >= 0.0F) ? (x1 - (int)x1) : (x1 - (int)x1 + 1.0F);
      float y1 = (float)(this.a * Math.sin((2.0F * my)) + this.b * Math.sin((2.0F * my)) * Math.cos((2.0F * mx)) + this.c * Math.sin((4.0F * my)) + this.d * Math.sin((6.0F * my)) * Math.cos((4.0F * mx)) + (this.k * y));
      y1 = (y1 >= 0.0F) ? (y1 - (int)y1) : (y1 - (int)y1 + 1.0F);
      x = x1;
      y = y1;
      int ix = (int)(width * x);
      int iy = (int)(height * y);
      if (ix >= 0 && ix < width && iy >= 0 && iy < height) {
        outPixels[width * iy + ix] = outPixels[width * iy + ix] + 1;
        int t = outPixels[width * iy + ix];
        if (t > max)
          max = t; 
      } 
    } 
    if (this.colormap != null) {
      int index = 0;
      for (y = 0.0F; y < height; y++) {
        for (x = 0.0F; x < width; x++) {
          outPixels[index] = this.colormap.getColor(outPixels[index] / max);
          index++;
        } 
      } 
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Texture/Chaotic Quilt...";
  }
}
