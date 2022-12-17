package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.io.Serializable;

public class MarbleTexFilter extends PointFilter implements Serializable {
  private float scale = 32.0F;
  
  private float stretch = 1.0F;
  
  private float angle = 0.0F;
  
  private float turbulence = 1.0F;
  
  private float turbulenceFactor = 0.5F;
  
  private Colormap colormap;
  
  private float m00 = 1.0F;
  
  private float m01 = 0.0F;
  
  private float m10 = 0.0F;
  
  private float m11 = 1.0F;
  
  public void setScale(float scale) {
    this.scale = scale;
  }
  
  public float getScale() {
    return this.scale;
  }
  
  public void setStretch(float stretch) {
    this.stretch = stretch;
  }
  
  public float getStretch() {
    return this.stretch;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
    float cos = (float)Math.cos(angle);
    float sin = (float)Math.sin(angle);
    this.m00 = cos;
    this.m01 = sin;
    this.m10 = -sin;
    this.m11 = cos;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setTurbulence(float turbulence) {
    this.turbulence = turbulence;
  }
  
  public float getTurbulence() {
    return this.turbulence;
  }
  
  public void setTurbulenceFactor(float turbulenceFactor) {
    this.turbulenceFactor = turbulenceFactor;
  }
  
  public float getTurbulenceFactor() {
    return this.turbulenceFactor;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    float nx = this.m00 * x + this.m01 * y;
    float ny = this.m10 * x + this.m11 * y;
    nx /= this.scale * this.stretch;
    ny /= this.scale;
    int a = rgb & 0xFF000000;
    if (this.colormap != null) {
      float f1 = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
      float f = 3.0F * this.turbulenceFactor * f1 + ny;
      f = (float)Math.sin(f * Math.PI);
      float f2 = (float)Math.sin(40.0D * f1);
      f = (float)(f + 0.2D * f2);
      return this.colormap.getColor(f);
    } 
    float chaos = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
    float t = (float)Math.sin(Math.sin(8.0D * chaos + (7.0F * nx) + 3.0D * ny));
    float brownLayer = Math.abs(t), greenLayer = brownLayer;
    float perturb = (float)Math.sin(40.0D * chaos);
    perturb = Math.abs(perturb);
    float brownPerturb = 0.6F * perturb + 0.3F;
    float greenPerturb = 0.2F * perturb + 0.8F;
    float grnPerturb = 0.15F * perturb + 0.85F;
    float grn = 0.5F * (float)Math.pow(Math.abs(brownLayer), 0.3D);
    brownLayer = (float)Math.pow(0.5D * (brownLayer + 1.0D), 0.6D) * brownPerturb;
    greenLayer = (float)Math.pow(0.5D * (greenLayer + 1.0D), 0.6D) * greenPerturb;
    float red = (0.5F * brownLayer + 0.35F * greenLayer) * 2.0F * grn;
    float blu = (0.25F * brownLayer + 0.35F * greenLayer) * 2.0F * grn;
    grn *= Math.max(brownLayer, greenLayer) * grnPerturb;
    int r = rgb >> 16 & 0xFF;
    int g = rgb >> 8 & 0xFF;
    int b = rgb & 0xFF;
    r = PixelUtils.clamp((int)(r * red));
    g = PixelUtils.clamp((int)(g * grn));
    b = PixelUtils.clamp((int)(b * blu));
    return rgb & 0xFF000000 | r << 16 | g << 8 | b;
  }
  
  public String toString() {
    return "Texture/Marble Texture...";
  }
}
