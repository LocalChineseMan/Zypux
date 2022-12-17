package com.jhlabs.image;

import java.io.Serializable;

public class WeaveFilter extends PointFilter implements Serializable {
  static final long serialVersionUID = 4847932412277504482L;
  
  private float xWidth = 16.0F;
  
  private float yWidth = 16.0F;
  
  private float xGap = 6.0F;
  
  private float yGap = 6.0F;
  
  private int rows = 4;
  
  private int cols = 4;
  
  private int rgbX = -32640;
  
  private int rgbY = -8355585;
  
  private boolean useImageColors = true;
  
  private boolean roundThreads = false;
  
  private boolean shadeCrossings = true;
  
  public int[][] matrix = new int[][] { { 0, 1, 0, 1 }, { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 1, 0, 1, 0 } };
  
  public void setXGap(float xGap) {
    this.xGap = xGap;
  }
  
  public void setXWidth(float xWidth) {
    this.xWidth = xWidth;
  }
  
  public float getXWidth() {
    return this.xWidth;
  }
  
  public void setYWidth(float yWidth) {
    this.yWidth = yWidth;
  }
  
  public float getYWidth() {
    return this.yWidth;
  }
  
  public float getXGap() {
    return this.xGap;
  }
  
  public void setYGap(float yGap) {
    this.yGap = yGap;
  }
  
  public float getYGap() {
    return this.yGap;
  }
  
  public void setCrossings(int[][] matrix) {
    this.matrix = matrix;
  }
  
  public int[][] getCrossings() {
    return this.matrix;
  }
  
  public void setUseImageColors(boolean useImageColors) {
    this.useImageColors = useImageColors;
  }
  
  public boolean getUseImageColors() {
    return this.useImageColors;
  }
  
  public void setRoundThreads(boolean roundThreads) {
    this.roundThreads = roundThreads;
  }
  
  public boolean getRoundThreads() {
    return this.roundThreads;
  }
  
  public void setShadeCrossings(boolean shadeCrossings) {
    this.shadeCrossings = shadeCrossings;
  }
  
  public boolean getShadeCrossings() {
    return this.shadeCrossings;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    float dX, dY, cX, cY;
    int lrgbX, lrgbY, v;
    x = (int)(x + this.xWidth + this.xGap / 2.0F);
    y = (int)(y + this.yWidth + this.yGap / 2.0F);
    float nx = ImageMath.mod(x, this.xWidth + this.xGap);
    float ny = ImageMath.mod(y, this.yWidth + this.yGap);
    int ix = (int)(x / (this.xWidth + this.xGap));
    int iy = (int)(y / (this.yWidth + this.yGap));
    boolean inX = (nx < this.xWidth);
    boolean inY = (ny < this.yWidth);
    if (this.roundThreads) {
      dX = Math.abs(this.xWidth / 2.0F - nx) / this.xWidth / 2.0F;
      dY = Math.abs(this.yWidth / 2.0F - ny) / this.yWidth / 2.0F;
    } else {
      dX = dY = 0.0F;
    } 
    if (this.shadeCrossings) {
      cX = ImageMath.smoothStep(this.xWidth / 2.0F, this.xWidth / 2.0F + this.xGap, Math.abs(this.xWidth / 2.0F - nx));
      cY = ImageMath.smoothStep(this.yWidth / 2.0F, this.yWidth / 2.0F + this.yGap, Math.abs(this.yWidth / 2.0F - ny));
    } else {
      cX = cY = 0.0F;
    } 
    if (this.useImageColors) {
      lrgbX = lrgbY = rgb;
    } else {
      lrgbX = this.rgbX;
      lrgbY = this.rgbY;
    } 
    int ixc = ix % this.cols;
    int iyr = iy % this.rows;
    int m = this.matrix[iyr][ixc];
    if (inX) {
      if (inY) {
        v = (m == 1) ? lrgbX : lrgbY;
        v = ImageMath.mixColors(2.0F * ((m == 1) ? dX : dY), v, -16777216);
      } else {
        if (this.shadeCrossings)
          if (m != this.matrix[(iy + 1) % this.rows][ixc]) {
            if (m == 0)
              cY = 1.0F - cY; 
            cY *= 0.5F;
            lrgbX = ImageMath.mixColors(cY, lrgbX, -16777216);
          } else if (m == 0) {
            lrgbX = ImageMath.mixColors(0.5F, lrgbX, -16777216);
          }  
        v = ImageMath.mixColors(2.0F * dX, lrgbX, -16777216);
      } 
    } else if (inY) {
      if (this.shadeCrossings)
        if (m != this.matrix[iyr][(ix + 1) % this.cols]) {
          if (m == 1)
            cX = 1.0F - cX; 
          cX *= 0.5F;
          lrgbY = ImageMath.mixColors(cX, lrgbY, -16777216);
        } else if (m == 1) {
          lrgbY = ImageMath.mixColors(0.5F, lrgbY, -16777216);
        }  
      v = ImageMath.mixColors(2.0F * dY, lrgbY, -16777216);
    } else {
      v = 0;
    } 
    return v;
  }
  
  public String toString() {
    return "Texture/Weave...";
  }
}
