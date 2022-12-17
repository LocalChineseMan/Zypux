package com.jhlabs.vecmath;

public class Quat4f extends Tuple4f {
  public Quat4f() {
    this(0.0F, 0.0F, 0.0F, 0.0F);
  }
  
  public Quat4f(float[] x) {
    this.x = x[0];
    this.y = x[1];
    this.z = x[2];
    this.w = x[3];
  }
  
  public Quat4f(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
  
  public Quat4f(Quat4f t) {
    this.x = t.x;
    this.y = t.y;
    this.z = t.z;
    this.w = t.w;
  }
  
  public Quat4f(Tuple4f t) {
    this.x = t.x;
    this.y = t.y;
    this.z = t.z;
    this.w = t.w;
  }
  
  public void set(AxisAngle4f a) {
    float halfTheta = a.angle * 0.5F;
    float cosHalfTheta = (float)Math.cos(halfTheta);
    float sinHalfTheta = (float)Math.sin(halfTheta);
    this.x = a.x * sinHalfTheta;
    this.y = a.y * sinHalfTheta;
    this.z = a.z * sinHalfTheta;
    this.w = cosHalfTheta;
  }
  
  public void normalize() {
    float d = 1.0F / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    this.x *= d;
    this.y *= d;
    this.z *= d;
    this.w *= d;
  }
  
  public void set(Matrix4f m) {
    float tr = m.m00 + m.m11 + m.m22;
    if (tr > 0.0D) {
      float s = (float)Math.sqrt((tr + 1.0F));
      this.w = s / 2.0F;
      s = 0.5F / s;
      this.x = (m.m12 - m.m21) * s;
      this.y = (m.m20 - m.m02) * s;
      this.z = (m.m01 - m.m10) * s;
    } else {
      float s;
      int i = 0;
      if (m.m11 > m.m00) {
        i = 1;
        if (m.m22 > m.m11)
          i = 2; 
      } else if (m.m22 > m.m00) {
        i = 2;
      } 
      switch (i) {
        case 0:
          s = (float)Math.sqrt((m.m00 - m.m11 + m.m22 + 1.0F));
          this.x = s * 0.5F;
          if (s != 0.0D)
            s = 0.5F / s; 
          this.w = (m.m12 - m.m21) * s;
          this.y = (m.m01 + m.m10) * s;
          this.z = (m.m02 + m.m20) * s;
          break;
        case 1:
          s = (float)Math.sqrt((m.m11 - m.m22 + m.m00 + 1.0F));
          this.y = s * 0.5F;
          if (s != 0.0D)
            s = 0.5F / s; 
          this.w = (m.m20 - m.m02) * s;
          this.z = (m.m12 + m.m21) * s;
          this.x = (m.m10 + m.m01) * s;
          break;
        case 2:
          s = (float)Math.sqrt((m.m00 - m.m11 + m.m22 + 1.0F));
          this.z = s * 0.5F;
          if (s != 0.0D)
            s = 0.5F / s; 
          this.w = (m.m01 - m.m10) * s;
          this.x = (m.m20 + m.m02) * s;
          this.y = (m.m21 + m.m12) * s;
          break;
      } 
    } 
  }
}
