package com.jhlabs.composite;

import java.awt.image.ColorModel;

class Context extends RGBComposite.RGBCompositeContext {
  public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
    super(alpha, srcColorModel, dstColorModel);
  }
  
  public void composeRGB(int[] src, int[] dst, float alpha) {
    int w = src.length;
    for (int i = 0; i < w; i += 4) {
      int sr = src[i];
      int dir = dst[i];
      int sg = src[i + 1];
      int dig = dst[i + 1];
      int sb = src[i + 2];
      int dib = dst[i + 2];
      int sa = src[i + 3];
      int dia = dst[i + 3];
      int dor = dir + RGBComposite.RGBCompositeContext.multiply255(sr, 255 - dir - dir);
      int dog = dig + RGBComposite.RGBCompositeContext.multiply255(sg, 255 - dig - dig);
      int dob = dib + RGBComposite.RGBCompositeContext.multiply255(sb, 255 - dib - dib);
      float a = alpha * sa / 255.0F;
      float ac = 1.0F - a;
      dst[i] = (int)(a * dor + ac * dir);
      dst[i + 1] = (int)(a * dog + ac * dig);
      dst[i + 2] = (int)(a * dob + ac * dib);
      dst[i + 3] = (int)(sa * alpha + dia * ac);
    } 
  }
  
  static class ExclusionComposite {}
}
