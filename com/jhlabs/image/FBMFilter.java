package com.jhlabs.image;

import com.jhlabs.math.CellularFunction2D;
import com.jhlabs.math.FBM;
import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;
import com.jhlabs.math.RidgedFBM;
import com.jhlabs.math.SCNoise;
import com.jhlabs.math.VLNoise;
import java.awt.image.BufferedImage;
import java.util.Random;

public class FBMFilter extends PointFilter implements Cloneable {
  public static final int NOISE = 0;
  
  public static final int RIDGED = 1;
  
  public static final int VLNOISE = 2;
  
  public static final int SCNOISE = 3;
  
  public static final int CELLULAR = 4;
  
  private float scale = 32.0F;
  
  private float stretch = 1.0F;
  
  private float angle = 0.0F;
  
  private float amount = 1.0F;
  
  private float H = 1.0F;
  
  private float octaves = 4.0F;
  
  private float lacunarity = 2.0F;
  
  private float gain = 0.5F;
  
  private float bias = 0.5F;
  
  private int operation;
  
  private float m00 = 1.0F;
  
  private float m01 = 0.0F;
  
  private float m10 = 0.0F;
  
  private float m11 = 1.0F;
  
  private float min;
  
  private float max;
  
  private Colormap colormap = new Gradient();
  
  private boolean ridged;
  
  private FBM fBm;
  
  protected Random random = new Random();
  
  private int basisType = 0;
  
  private Function2D basis;
  
  public FBMFilter() {
    setBasisType(0);
  }
  
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  public float getAmount() {
    return this.amount;
  }
  
  public void setOperation(int operation) {
    this.operation = operation;
  }
  
  public int getOperation() {
    return this.operation;
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
    float cos = (float)Math.cos(this.angle);
    float sin = (float)Math.sin(this.angle);
    this.m00 = cos;
    this.m01 = sin;
    this.m10 = -sin;
    this.m11 = cos;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setOctaves(float octaves) {
    this.octaves = octaves;
  }
  
  public float getOctaves() {
    return this.octaves;
  }
  
  public void setH(float H) {
    this.H = H;
  }
  
  public float getH() {
    return this.H;
  }
  
  public void setLacunarity(float lacunarity) {
    this.lacunarity = lacunarity;
  }
  
  public float getLacunarity() {
    return this.lacunarity;
  }
  
  public void setGain(float gain) {
    this.gain = gain;
  }
  
  public float getGain() {
    return this.gain;
  }
  
  public void setBias(float bias) {
    this.bias = bias;
  }
  
  public float getBias() {
    return this.bias;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public void setBasisType(int basisType) {
    this.basisType = basisType;
    switch (basisType) {
      default:
        this.basis = (Function2D)new Noise();
        return;
      case 1:
        this.basis = (Function2D)new RidgedFBM();
        return;
      case 2:
        this.basis = (Function2D)new VLNoise();
        return;
      case 3:
        this.basis = (Function2D)new SCNoise();
        return;
      case 4:
        break;
    } 
    this.basis = (Function2D)new CellularFunction2D();
  }
  
  public int getBasisType() {
    return this.basisType;
  }
  
  public void setBasis(Function2D basis) {
    this.basis = basis;
  }
  
  public Function2D getBasis() {
    return this.basis;
  }
  
  protected FBM makeFBM(float H, float lacunarity, float octaves) {
    FBM fbm = new FBM(H, lacunarity, octaves, this.basis);
    float[] minmax = Noise.findRange((Function2D)fbm, null);
    this.min = minmax[0];
    this.max = minmax[1];
    return fbm;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    this.fBm = makeFBM(this.H, this.lacunarity, this.octaves);
    return super.filter(src, dst);
  }
  
  public int filterRGB(int x, int y, int rgb) {
    int v;
    float nx = this.m00 * x + this.m01 * y;
    float ny = this.m10 * x + this.m11 * y;
    nx /= this.scale;
    ny /= this.scale * this.stretch;
    float f = this.fBm.evaluate(nx, ny);
    f = (f - this.min) / (this.max - this.min);
    f = ImageMath.gain(f, this.gain);
    f = ImageMath.bias(f, this.bias);
    f *= this.amount;
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
    if (this.operation != 0)
      v = PixelUtils.combinePixels(rgb, v, this.operation); 
    return v;
  }
  
  public String toString() {
    return "Texture/Fractal Brownian Motion...";
  }
}
