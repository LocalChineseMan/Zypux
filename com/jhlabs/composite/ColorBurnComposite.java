package com.jhlabs.composite;

import java.awt.image.ColorModel;

class Context extends RGBComposite.RGBCompositeContext {
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
      if (sr != 0) {
        dor = Math.max(255 - (255 - dir << 8) / sr, 0);
      } else {
        dor = sr;
      } 
      if (sg != 0) {
        dog = Math.max(255 - (255 - dig << 8) / sg, 0);
      } else {
        dog = sg;
      } 
      if (sb != 0) {
        dob = Math.max(255 - (255 - dib << 8) / sb, 0);
      } else {
        dob = sb;
      } 
      float a = alpha * sa / 255.0F;
      float ac = 1.0F - a;
      dst[i] = (int)(a * dor + ac * dir);
      dst[i + 1] = (int)(a * dog + ac * dig);
      dst[i + 2] = (int)(a * dob + ac * dib);
      dst[i + 3] = (int)(sa * alpha + dia * ac);
    } 
  }
  
  static class ColorBurnComposite {}
}
