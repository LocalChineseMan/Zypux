package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class HalftoneFilter extends AbstractBufferedImageOp {
  private float density = 0.0F;
  
  private float softness = 0.0F;
  
  private boolean invert;
  
  private BufferedImage mask;
  
  public void setDensity(float density) {
    this.density = density;
  }
  
  public float getDensity() {
    return this.density;
  }
  
  public void setSoftness(float softness) {
    this.softness = softness;
  }
  
  public float getSoftness() {
    return this.softness;
  }
  
  public void setMask(BufferedImage mask) {
    this.mask = mask;
  }
  
  public BufferedImage getMask() {
    return this.mask;
  }
  
  public void setInvert(boolean invert) {
    this.invert = invert;
  }
  
  public boolean getInvert() {
    return this.invert;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int width = src.getWidth();
    int height = src.getHeight();
    if (dst == null)
      dst = createCompatibleDestImage(src, null); 
    if (this.mask == null)
      return dst; 
    int maskWidth = this.mask.getWidth();
    int maskHeight = this.mask.getHeight();
    float d = this.density * (1.0F + this.softness);
    float lower = 255.0F * (d - this.softness);
    float upper = 255.0F * d;
    float s = 255.0F * this.softness;
    int[] inPixels = new int[width];
    int[] maskPixels = new int[maskWidth];
    for (int y = 0; y < height; y++) {
      getRGB(src, 0, y, width, 1, inPixels);
      getRGB(this.mask, 0, y % maskHeight, maskWidth, 1, maskPixels);
      for (int x = 0; x < width; x++) {
        int maskRGB = maskPixels[x % maskWidth];
        int inRGB = inPixels[x];
        int v = PixelUtils.brightness(maskRGB);
        int iv = PixelUtils.brightness(inRGB);
        float f = ImageMath.smoothStep(iv - s, iv + s, v);
        int a = (int)(255.0F * f);
        if (this.invert)
          a = 255 - a; 
        inPixels[x] = inRGB & 0xFF000000 | a << 16 | a << 8 | a;
      } 
      setRGB(dst, 0, y, width, 1, inPixels);
    } 
    return dst;
  }
  
  public String toString() {
    return "Stylize/Halftone...";
  }
}
