package com.jhlabs.image;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.ImageFunction2D;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Tuple3f;
import com.jhlabs.vecmath.Tuple4f;
import com.jhlabs.vecmath.Vector3f;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.Serializable;

public class ShadeFilter extends WholeImageFilter implements Serializable {
  public static final int COLORS_FROM_IMAGE = 0;
  
  public static final int COLORS_CONSTANT = 1;
  
  public static final int BUMPS_FROM_IMAGE = 0;
  
  public static final int BUMPS_FROM_IMAGE_ALPHA = 1;
  
  public static final int BUMPS_FROM_MAP = 2;
  
  public static final int BUMPS_FROM_BEVEL = 3;
  
  private float bumpHeight;
  
  private float bumpSoftness;
  
  private float viewDistance = 10000.0F;
  
  private int colorSource = 0;
  
  private int bumpSource = 0;
  
  private Function2D bumpFunction;
  
  private BufferedImage environmentMap;
  
  private int[] envPixels;
  
  private int envWidth = 1;
  
  private int envHeight = 1;
  
  private Vector3f l;
  
  private Vector3f v;
  
  private Vector3f n;
  
  private Color4f shadedColor;
  
  private Color4f diffuse_color;
  
  private Color4f specular_color;
  
  private Vector3f tmpv;
  
  private Vector3f tmpv2;
  
  protected static final float r255 = 0.003921569F;
  
  public ShadeFilter() {
    this.bumpHeight = 1.0F;
    this.bumpSoftness = 5.0F;
    this.l = new Vector3f();
    this.v = new Vector3f();
    this.n = new Vector3f();
    this.shadedColor = new Color4f();
    this.diffuse_color = new Color4f();
    this.specular_color = new Color4f();
    this.tmpv = new Vector3f();
    this.tmpv2 = new Vector3f();
  }
  
  public void setBumpFunction(Function2D bumpFunction) {
    this.bumpFunction = bumpFunction;
  }
  
  public Function2D getBumpFunction() {
    return this.bumpFunction;
  }
  
  public void setBumpHeight(float bumpHeight) {
    this.bumpHeight = bumpHeight;
  }
  
  public float getBumpHeight() {
    return this.bumpHeight;
  }
  
  public void setBumpSoftness(float bumpSoftness) {
    this.bumpSoftness = bumpSoftness;
  }
  
  public float getBumpSoftness() {
    return this.bumpSoftness;
  }
  
  public void setEnvironmentMap(BufferedImage environmentMap) {
    this.environmentMap = environmentMap;
    if (environmentMap != null) {
      this.envWidth = environmentMap.getWidth();
      this.envHeight = environmentMap.getHeight();
      this.envPixels = getRGB(environmentMap, 0, 0, this.envWidth, this.envHeight, null);
    } else {
      this.envWidth = this.envHeight = 1;
      this.envPixels = null;
    } 
  }
  
  public BufferedImage getEnvironmentMap() {
    return this.environmentMap;
  }
  
  public void setBumpSource(int bumpSource) {
    this.bumpSource = bumpSource;
  }
  
  public int getBumpSource() {
    return this.bumpSource;
  }
  
  protected void setFromRGB(Color4f c, int argb) {
    c.set((argb >> 16 & 0xFF) * 0.003921569F, (argb >> 8 & 0xFF) * 0.003921569F, (argb & 0xFF) * 0.003921569F, (argb >> 24 & 0xFF) * 0.003921569F);
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    ImageFunction2D imageFunction2D;
    int index = 0;
    int[] outPixels = new int[width * height];
    float width45 = Math.abs(6.0F * this.bumpHeight);
    boolean invertBumps = (this.bumpHeight < 0.0F);
    Vector3f position = new Vector3f(0.0F, 0.0F, 0.0F);
    Vector3f viewpoint = new Vector3f(width / 2.0F, height / 2.0F, this.viewDistance);
    Vector3f normal = new Vector3f();
    Color4f c = new Color4f();
    Function2D bump = this.bumpFunction;
    if (this.bumpSource == 0 || this.bumpSource == 1 || this.bumpSource == 2 || bump == null)
      if (this.bumpSoftness != 0.0F) {
        int bumpWidth = width;
        int bumpHeight = height;
        int[] bumpPixels = inPixels;
        if (this.bumpSource == 2 && this.bumpFunction instanceof ImageFunction2D) {
          ImageFunction2D if2d = (ImageFunction2D)this.bumpFunction;
          bumpWidth = if2d.getWidth();
          bumpHeight = if2d.getHeight();
          bumpPixels = if2d.getPixels();
        } 
        Kernel kernel = GaussianFilter.makeKernel(this.bumpSoftness);
        int[] tmpPixels = new int[bumpWidth * bumpHeight];
        int[] softPixels = new int[bumpWidth * bumpHeight];
        GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, ConvolveFilter.CLAMP_EDGES);
        GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, ConvolveFilter.CLAMP_EDGES);
        imageFunction2D = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, 1, (this.bumpSource == 1));
      } else {
        imageFunction2D = new ImageFunction2D(inPixels, width, height, 1, (this.bumpSource == 1));
      }  
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    Vector3f n = new Vector3f();
    for (int y = 0; y < height; y++) {
      float ny = y;
      ((Tuple3f)position).y = y;
      for (int x = 0; x < width; x++) {
        float nx = x;
        if (this.bumpSource != 3) {
          int count = 0;
          ((Tuple3f)normal).x = ((Tuple3f)normal).y = ((Tuple3f)normal).z = 0.0F;
          float m0 = width45 * imageFunction2D.evaluate(nx, ny);
          float m1 = (x > 0) ? (width45 * imageFunction2D.evaluate(nx - 1.0F, ny) - m0) : -2.0F;
          float m2 = (y > 0) ? (width45 * imageFunction2D.evaluate(nx, ny - 1.0F) - m0) : -2.0F;
          float m3 = (x < width - 1) ? (width45 * imageFunction2D.evaluate(nx + 1.0F, ny) - m0) : -2.0F;
          float m4 = (y < height - 1) ? (width45 * imageFunction2D.evaluate(nx, ny + 1.0F) - m0) : -2.0F;
          if (m1 != -2.0F && m4 != -2.0F) {
            ((Tuple3f)v1).x = -1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m1;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = 1.0F;
            ((Tuple3f)v2).z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (m1 != -2.0F && m2 != -2.0F) {
            ((Tuple3f)v1).x = -1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m1;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = -1.0F;
            ((Tuple3f)v2).z = m2;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (m2 != -2.0F && m3 != -2.0F) {
            ((Tuple3f)v1).x = 0.0F;
            ((Tuple3f)v1).y = -1.0F;
            ((Tuple3f)v1).z = m2;
            ((Tuple3f)v2).x = 1.0F;
            ((Tuple3f)v2).y = 0.0F;
            ((Tuple3f)v2).z = m3;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (m3 != -2.0F && m4 != -2.0F) {
            ((Tuple3f)v1).x = 1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m3;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = 1.0F;
            ((Tuple3f)v2).z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          ((Tuple3f)normal).x /= count;
          ((Tuple3f)normal).y /= count;
          ((Tuple3f)normal).z /= count;
        } 
        if (invertBumps) {
          ((Tuple3f)normal).x = -((Tuple3f)normal).x;
          ((Tuple3f)normal).y = -((Tuple3f)normal).y;
        } 
        ((Tuple3f)position).x = x;
        if (((Tuple3f)normal).z >= 0.0F) {
          if (this.environmentMap != null) {
            this.tmpv2.set((Tuple3f)viewpoint);
            this.tmpv2.sub((Tuple3f)position);
            this.tmpv2.normalize();
            this.tmpv.set((Tuple3f)normal);
            this.tmpv.normalize();
            this.tmpv.scale(2.0F * this.tmpv.dot(this.tmpv2));
            this.tmpv.sub((Tuple3f)this.v);
            this.tmpv.normalize();
            setFromRGB(c, getEnvironmentMapP(normal, inPixels, width, height));
            int alpha = inPixels[index] & 0xFF000000;
            int rgb = (int)(((Tuple4f)c).x * 255.0F) << 16 | (int)(((Tuple4f)c).y * 255.0F) << 8 | (int)(((Tuple4f)c).z * 255.0F);
            outPixels[index++] = alpha | rgb;
          } else {
            outPixels[index++] = 0;
          } 
        } else {
          outPixels[index++] = 0;
        } 
      } 
    } 
    return outPixels;
  }
  
  private int getEnvironmentMapP(Vector3f normal, int[] inPixels, int width, int height) {
    if (this.environmentMap != null) {
      float x = 0.5F * (1.0F + ((Tuple3f)normal).x);
      float y = 0.5F * (1.0F + ((Tuple3f)normal).y);
      x = ImageMath.clamp(x * this.envWidth, 0.0F, (this.envWidth - 1));
      y = ImageMath.clamp(y * this.envHeight, 0.0F, (this.envHeight - 1));
      int ix = (int)x;
      int iy = (int)y;
      float xWeight = x - ix;
      float yWeight = y - iy;
      int i = this.envWidth * iy + ix;
      int dx = (ix == this.envWidth - 1) ? 0 : 1;
      int dy = (iy == this.envHeight - 1) ? 0 : this.envWidth;
      return ImageMath.bilinearInterpolate(xWeight, yWeight, this.envPixels[i], this.envPixels[i + dx], this.envPixels[i + dy], this.envPixels[i + dx + dy]);
    } 
    return 0;
  }
  
  public String toString() {
    return "Stylize/Shade...";
  }
}
