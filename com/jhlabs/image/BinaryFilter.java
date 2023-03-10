package com.jhlabs.image;

import com.jhlabs.math.BinaryFunction;
import com.jhlabs.math.BlackFunction;

public abstract class BinaryFilter extends WholeImageFilter {
  protected int newColor = -16777216;
  
  protected BinaryFunction blackFunction = (BinaryFunction)new BlackFunction();
  
  protected int iterations = 1;
  
  protected Colormap colormap;
  
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
  
  public int getIterations() {
    return this.iterations;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public void setNewColor(int newColor) {
    this.newColor = newColor;
  }
  
  public int getNewColor() {
    return this.newColor;
  }
  
  public void setBlackFunction(BinaryFunction blackFunction) {
    this.blackFunction = blackFunction;
  }
  
  public BinaryFunction getBlackFunction() {
    return this.blackFunction;
  }
}
