package com.jhlabs.image;

import java.io.Serializable;

public class SpectrumColormap implements Colormap, Serializable {
  public int getColor(float v) {
    return Spectrum.wavelengthToRGB(380.0F + 400.0F * ImageMath.clamp(v, 0.0F, 1.0F));
  }
}
