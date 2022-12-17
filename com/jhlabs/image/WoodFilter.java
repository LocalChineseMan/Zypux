package com.jhlabs.image;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;

public class WoodFilter extends PointFilter {
  private float scale = 200.0F;
  
  private float stretch = 10.0F;
  
  private float angle = 1.5707964F;
  
  public float rings = 0.5F;
  
  public float turbulence = 0.0F;
  
  public float fibres = 0.5F;
  
  public float gain = 0.8F;
  
  private float m00 = 1.0F;
  
  private float m01 = 0.0F;
  
  private float m10 = 0.0F;
  
  private float m11 = 1.0F;
  
  private Colormap colormap = new LinearColormap(-1719148, -6784175);
  
  private Function2D function = (Function2D)new Noise();
  
  public void setRings(float rings) {
    this.rings = rings;
  }
  
  public float getRings() {
    return this.rings;
  }
  
  public void setFunction(Function2D function) {
    this.function = function;
  }
  
  public Function2D getFunction() {
    return this.function;
  }
  
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
  
  public void setFibres(float fibres) {
    this.fibres = fibres;
  }
  
  public float getFibres() {
    return this.fibres;
  }
  
  public void setgain(float gain) {
    this.gain = gain;
  }
  
  public float getGain() {
    return this.gain;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int v;
    float nx = this.m00 * x + this.m01 * y;
    float ny = this.m10 * x + this.m11 * y;
    nx /= this.scale;
    ny /= this.scale * this.stretch;
    float f = Noise.noise2(nx, ny);
    f += 0.1F * this.turbulence * Noise.noise2(nx * 0.05F, ny * 20.0F);
    f = f * 0.5F + 0.5F;
    f *= this.rings * 50.0F;
    f -= (int)f;
    f *= 1.0F - ImageMath.smoothStep(this.gain, 1.0F, f);
    f += this.fibres * Noise.noise2(nx * this.scale, ny * 50.0F);
    int a = rgb & 0xFF000000;
    if (this.colormap != null) {
      v = this.colormap.getColor(f);
    } else {
      v = PixelUtils.clamp((int)(f * 255.0F));
      int r = v << 16;
      int g = v << 8;
      int b = v;
      v = a | r | g | b;
    } 
    return v;
  }
  
  public String toString() {
    return "Texture/Wood...";
  }
}
