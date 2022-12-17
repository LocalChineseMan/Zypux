package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class IteratedFilter extends AbstractBufferedImageOp {
  private BufferedImageOp filter;
  
  private int iterations;
  
  public IteratedFilter(BufferedImageOp filter, int iterations) {
    this.filter = filter;
    this.iterations = iterations;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    BufferedImage image = src;
    for (int i = 0; i < this.iterations; i++)
      image = this.filter.filter(image, dst); 
    return image;
  }
}
