package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class OverlayComposite extends RGBComposite {
  public OverlayComposite(float alpha) {
    super(alpha);
  }
  
  public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
    return new Context(this.extraAlpha, srcColorModel, dstColorModel);
  }
  
  static class Context extends RGBComposite.RGBCompositeContext {
    public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
      super(alpha, srcColorModel, dstColorModel);
    }
    
    public void composeRGB(int[] src, int[] dst, float alpha) {
      int w = src.length;
      for (int i = 0; i < w; i += 4) {
        int dor, dog, dob, sr = src[i];
        int dir = dst[i];
        int sg = src[i + 1];
        int dig = dst[i + 1];
        int sb = src[i + 2];
        int dib = dst[i + 2];
        int sa = src[i + 3];
        int dia = dst[i + 3];
        if (dir < 128) {
          int t = dir * sr + 128;
          dor = 2 * ((t >> 8) + t >> 8);
        } else {
          int t = (255 - dir) * (255 - sr) + 128;
          dor = 2 * (255 - ((t >> 8) + t >> 8));
        } 
        if (dig < 128) {
          int j = dig * sg + 128;
          dog = 2 * ((j >> 8) + j >> 8);
        } else {
          int j = (255 - dig) * (255 - sg) + 128;
          dog = 2 * (255 - ((j >> 8) + j >> 8));
        } 
        if (dib < 128) {
          int j = dib * sb + 128;
          dob = 2 * ((j >> 8) + j >> 8);
        } else {
          int j = (255 - dib) * (255 - sb) + 128;
          dob = 2 * (255 - ((j >> 8) + j >> 8));
        } 
        float a = alpha * sa / 255.0F;
        float ac = 1.0F - a;
        dst[i] = (int)(a * dor + ac * dir);
        dst[i + 1] = (int)(a * dog + ac * dig);
        dst[i + 2] = (int)(a * dob + ac * dib);
        dst[i + 3] = (int)(sa * alpha + dia * ac);
      } 
    }
  }
}
