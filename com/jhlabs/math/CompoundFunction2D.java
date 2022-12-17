package com.jhlabs.math;

public abstract class CompoundFunction2D implements Function2D {
  protected Function2D basis;
  
  public CompoundFunction2D(Function2D basis) {
    this.basis = basis;
  }
  
  public void setBasis(Function2D basis) {
    this.basis = basis;
  }
  
  public Function2D getBasis() {
    return this.basis;
  }
  
  public abstract float evaluate(float paramFloat1, float paramFloat2);
}
