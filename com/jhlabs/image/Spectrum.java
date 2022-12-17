package com.jhlabs.image;

public class Spectrum {
  private static int adjust(float color, float factor, float gamma) {
    if (color == 0.0D)
      return 0; 
    return (int)Math.round(255.0D * Math.pow((color * factor), gamma));
  }
  
  public static int wavelengthToRGB(float wavelength) {
    float r, g, b, factor, gamma = 0.8F;
    int w = (int)wavelength;
    if (w < 380) {
      r = 0.0F;
      g = 0.0F;
      b = 0.0F;
    } else if (w < 440) {
      r = -(wavelength - 440.0F) / 60.0F;
      g = 0.0F;
      b = 1.0F;
    } else if (w < 490) {
      r = 0.0F;
      g = (wavelength - 440.0F) / 50.0F;
      b = 1.0F;
    } else if (w < 510) {
      r = 0.0F;
      g = 1.0F;
      b = -(wavelength - 510.0F) / 20.0F;
    } else if (w < 580) {
      r = (wavelength - 510.0F) / 70.0F;
      g = 1.0F;
      b = 0.0F;
    } else if (w < 645) {
      r = 1.0F;
      g = -(wavelength - 645.0F) / 65.0F;
      b = 0.0F;
    } else if (w <= 780) {
      r = 1.0F;
      g = 0.0F;
      b = 0.0F;
    } else {
      r = 0.0F;
      g = 0.0F;
      b = 0.0F;
    } 
    if (380 <= w && w <= 419) {
      factor = 0.3F + 0.7F * (wavelength - 380.0F) / 40.0F;
    } else if (420 <= w && w <= 700) {
      factor = 1.0F;
    } else if (701 <= w && w <= 780) {
      factor = 0.3F + 0.7F * (780.0F - wavelength) / 80.0F;
    } else {
      factor = 0.0F;
    } 
    int ir = adjust(r, factor, gamma);
    int ig = adjust(g, factor, gamma);
    int ib = adjust(b, factor, gamma);
    return 0xFF000000 | ir << 16 | ig << 8 | ib;
  }
}
