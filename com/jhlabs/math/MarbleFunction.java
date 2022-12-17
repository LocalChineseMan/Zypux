package com.jhlabs.math;

public class MarbleFunction extends CompoundFunction2D {
  public MarbleFunction() {
    super(new TurbulenceFunction(new Noise(), 6.0F));
  }
  
  public MarbleFunction(Function2D basis) {
    super(basis);
  }
  
  public float evaluate(float x, float y) {
    return (float)Math.pow(0.5D * (Math.sin(8.0D * this.basis.evaluate(x, y)) + 1.0D), 0.77D);
  }
}
