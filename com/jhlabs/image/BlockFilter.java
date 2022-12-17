package com.jhlabs.image;

import java.awt.Point;

public class BlockFilter extends TransformFilter {
  static final long serialVersionUID = 8077109551486196569L;
  
  private int blockSize = 2;
  
  public void setBlockSize(int blockSize) {
    this.blockSize = blockSize;
  }
  
  public int getBlockSize() {
    return this.blockSize;
  }
  
  protected void transform(int x, int y, Point out) {
    out.x = x / this.blockSize * this.blockSize;
    out.y = y / this.blockSize * this.blockSize;
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    out[0] = (x / this.blockSize * this.blockSize);
    out[1] = (y / this.blockSize * this.blockSize);
  }
  
  public String toString() {
    return "Stylize/Mosaic...";
  }
}
