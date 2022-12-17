package com.jhlabs.composite;

import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class SoftLightComposite extends RGBComposite {
  public SoftLightComposite(float alpha) {
    super(alpha);
  }
  
  public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
    return (CompositeContext)new Context(((RGBComposite)this).extraAlpha, srcColorModel, dstColorModel);
  }
  
  static class SoftLightComposite {}
}
