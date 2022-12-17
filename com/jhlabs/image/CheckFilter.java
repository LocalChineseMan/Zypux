package com.jhlabs.image;

public class CheckFilter extends PointFilter {
  private int xScale = 8;
  
  private int yScale = 8;
  
  private int foreground = -1;
  
  private int background = -16777216;
  
  private int fuzziness = 0;
  
  private float angle = 0.0F;
  
  private int operation;
  
  private float m00 = 1.0F;
  
  private float m01 = 0.0F;
  
  private float m10 = 0.0F;
  
  private float m11 = 1.0F;
  
  public void setForeground(int foreground) {
    this.foreground = foreground;
  }
  
  public int getForeground() {
    return this.foreground;
  }
  
  public void setBackground(int background) {
    this.background = background;
  }
  
  public int getBackground() {
    return this.background;
  }
  
  public void setXScale(int xScale) {
    this.xScale = xScale;
  }
  
  public int getXScale() {
    return this.xScale;
  }
  
  public void setYScale(int yScale) {
    this.yScale = yScale;
  }
  
  public int getYScale() {
    return this.yScale;
  }
  
  public void setFuzziness(int fuzziness) {
    this.fuzziness = fuzziness;
  }
  
  public int getFuzziness() {
    return this.fuzziness;
  }
  
  public void setOperation(int operation) {
    this.operation = operation;
  }
  
  public int getOperation() {
    return this.operation;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
    float cos = (float)Math.cos(angle);
    float sin = (float)Math.sin(angle);
    this.m00 = cos;
    this.m01 = sin;
    this.m10 = -sin;
    this.m11 = cos;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public int filterRGB(int x, int y, int rgb) {
    float nx = (this.m00 * x + this.m01 * y) / this.xScale;
    float ny = (this.m10 * x + this.m11 * y) / this.yScale;
    float f = ((int)(nx + 100000.0F) % 2 != (int)(ny + 100000.0F) % 2) ? 1.0F : 0.0F;
    if (this.fuzziness != 0) {
      float fuzz = this.fuzziness / 100.0F;
      float fx = ImageMath.smoothPulse(0.0F, fuzz, 1.0F - fuzz, 1.0F, ImageMath.mod(nx, 1.0F));
      float fy = ImageMath.smoothPulse(0.0F, fuzz, 1.0F - fuzz, 1.0F, ImageMath.mod(ny, 1.0F));
      f *= fx * fy;
    } 
    return ImageMath.mixColors(f, this.foreground, this.background);
  }
  
  public String toString() {
    return "Texture/Checkerboard...";
  }
}
