package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class ScaleFilter extends AbstractBufferedImageOp {
  private int width;
  
  private int height;
  
  public ScaleFilter() {
    this(32, 32);
  }
  
  public ScaleFilter(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int w = src.getWidth();
    int h = src.getHeight();
    if (dst == null) {
      ColorModel dstCM = src.getColorModel();
      dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(w, h), dstCM.isAlphaPremultiplied(), null);
    } 
    Image scaleImage = src.getScaledInstance(w, h, 16);
    Graphics2D g = dst.createGraphics();
    g.drawImage(src, 0, 0, this.width, this.height, null);
    g.dispose();
    return dst;
  }
  
  public String toString() {
    return "Distort/Scale";
  }
}
