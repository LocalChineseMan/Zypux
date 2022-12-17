package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.Serializable;

public abstract class WholeImageFilter extends AbstractBufferedImageOp implements Serializable {
  protected Rectangle transformedSpace;
  
  protected Rectangle originalSpace;
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int width = src.getWidth();
    int height = src.getHeight();
    int type = src.getType();
    WritableRaster srcRaster = src.getRaster();
    this.originalSpace = new Rectangle(0, 0, width, height);
    this.transformedSpace = new Rectangle(0, 0, width, height);
    transformSpace(this.transformedSpace);
    if (dst == null) {
      ColorModel dstCM = src.getColorModel();
      dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.transformedSpace.width, this.transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
    } 
    WritableRaster dstRaster = dst.getRaster();
    int[] inPixels = getRGB(src, 0, 0, width, height, null);
    inPixels = filterPixels(width, height, inPixels, this.transformedSpace);
    setRGB(dst, 0, 0, this.transformedSpace.width, this.transformedSpace.height, inPixels);
    return dst;
  }
  
  protected void transformSpace(Rectangle rect) {}
  
  protected abstract int[] filterPixels(int paramInt1, int paramInt2, int[] paramArrayOfint, Rectangle paramRectangle);
}
