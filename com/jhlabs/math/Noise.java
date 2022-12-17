package com.jhlabs.math;

import java.util.Random;

public class Noise implements Function1D, Function2D, Function3D {
  private static Random randomGenerator = new Random();
  
  private static final int B = 256;
  
  private static final int BM = 255;
  
  private static final int N = 4096;
  
  public float evaluate(float x) {
    return noise1(x);
  }
  
  public float evaluate(float x, float y) {
    return noise2(x, y);
  }
  
  public float evaluate(float x, float y, float z) {
    return noise3(x, y, z);
  }
  
  public static float turbulence2(float x, float y, float octaves) {
    float t = 0.0F;
    float f;
    for (f = 1.0F; f <= octaves; f *= 2.0F)
      t += Math.abs(noise2(f * x, f * y)) / f; 
    return t;
  }
  
  public static float turbulence3(float x, float y, float z, float octaves) {
    float t = 0.0F;
    float f;
    for (f = 1.0F; f <= octaves; f *= 2.0F)
      t += Math.abs(noise3(f * x, f * y, f * z)) / f; 
    return t;
  }
  
  static int[] p = new int[514];
  
  static float[][] g3 = new float[514][3];
  
  static float[][] g2 = new float[514][2];
  
  static float[] g1 = new float[514];
  
  static boolean start = true;
  
  private static float sCurve(float t) {
    return t * t * (3.0F - 2.0F * t);
  }
  
  public static float noise1(float x) {
    if (start) {
      start = false;
      init();
    } 
    float t = x + 4096.0F;
    int bx0 = (int)t & 0xFF;
    int bx1 = bx0 + 1 & 0xFF;
    float rx0 = t - (int)t;
    float rx1 = rx0 - 1.0F;
    float sx = sCurve(rx0);
    float u = rx0 * g1[p[bx0]];
    float v = rx1 * g1[p[bx1]];
    return 2.3F * lerp(sx, u, v);
  }
  
  public static float noise2(float x, float y) {
    if (start) {
      start = false;
      init();
    } 
    float t = x + 4096.0F;
    int bx0 = (int)t & 0xFF;
    int bx1 = bx0 + 1 & 0xFF;
    float rx0 = t - (int)t;
    float rx1 = rx0 - 1.0F;
    t = y + 4096.0F;
    int by0 = (int)t & 0xFF;
    int by1 = by0 + 1 & 0xFF;
    float ry0 = t - (int)t;
    float ry1 = ry0 - 1.0F;
    int i = p[bx0];
    int j = p[bx1];
    int b00 = p[i + by0];
    int b10 = p[j + by0];
    int b01 = p[i + by1];
    int b11 = p[j + by1];
    float sx = sCurve(rx0);
    float sy = sCurve(ry0);
    float q[] = g2[b00], u = rx0 * q[0] + ry0 * q[1];
    q = g2[b10];
    float v = rx1 * q[0] + ry0 * q[1];
    float a = lerp(sx, u, v);
    q = g2[b01];
    u = rx0 * q[0] + ry1 * q[1];
    q = g2[b11];
    v = rx1 * q[0] + ry1 * q[1];
    float b = lerp(sx, u, v);
    return 1.5F * lerp(sy, a, b);
  }
  
  public static float noise3(float x, float y, float z) {
    if (start) {
      start = false;
      init();
    } 
    float t = x + 4096.0F;
    int bx0 = (int)t & 0xFF;
    int bx1 = bx0 + 1 & 0xFF;
    float rx0 = t - (int)t;
    float rx1 = rx0 - 1.0F;
    t = y + 4096.0F;
    int by0 = (int)t & 0xFF;
    int by1 = by0 + 1 & 0xFF;
    float ry0 = t - (int)t;
    float ry1 = ry0 - 1.0F;
    t = z + 4096.0F;
    int bz0 = (int)t & 0xFF;
    int bz1 = bz0 + 1 & 0xFF;
    float rz0 = t - (int)t;
    float rz1 = rz0 - 1.0F;
    int i = p[bx0];
    int j = p[bx1];
    int b00 = p[i + by0];
    int b10 = p[j + by0];
    int b01 = p[i + by1];
    int b11 = p[j + by1];
    t = sCurve(rx0);
    float sy = sCurve(ry0);
    float sz = sCurve(rz0);
    float q[] = g3[b00 + bz0], u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2];
    q = g3[b10 + bz0];
    float v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2];
    float a = lerp(t, u, v);
    q = g3[b01 + bz0];
    u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2];
    q = g3[b11 + bz0];
    v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2];
    float b = lerp(t, u, v);
    float c = lerp(sy, a, b);
    q = g3[b00 + bz1];
    u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2];
    q = g3[b10 + bz1];
    v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2];
    a = lerp(t, u, v);
    q = g3[b01 + bz1];
    u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2];
    q = g3[b11 + bz1];
    v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2];
    b = lerp(t, u, v);
    float d = lerp(sy, a, b);
    return 1.5F * lerp(sz, c, d);
  }
  
  public static float lerp(float t, float a, float b) {
    return a + t * (b - a);
  }
  
  private static void normalize2(float[] v) {
    float s = (float)Math.sqrt((v[0] * v[0] + v[1] * v[1]));
    v[0] = v[0] / s;
    v[1] = v[1] / s;
  }
  
  static void normalize3(float[] v) {
    float s = (float)Math.sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
    v[0] = v[0] / s;
    v[1] = v[1] / s;
    v[2] = v[2] / s;
  }
  
  private static int random() {
    return randomGenerator.nextInt() & Integer.MAX_VALUE;
  }
  
  private static void init() {
    int i;
    for (i = 0; i < 256; i++) {
      p[i] = i;
      g1[i] = (random() % 512 - 256) / 256.0F;
      int j;
      for (j = 0; j < 2; j++)
        g2[i][j] = (random() % 512 - 256) / 256.0F; 
      normalize2(g2[i]);
      for (j = 0; j < 3; j++)
        g3[i][j] = (random() % 512 - 256) / 256.0F; 
      normalize3(g3[i]);
    } 
    for (i = 255; i >= 0; i--) {
      int k = p[i];
      int j;
      p[i] = p[j = random() % 256];
      p[j] = k;
    } 
    for (i = 0; i < 258; i++) {
      p[256 + i] = p[i];
      g1[256 + i] = g1[i];
      int j;
      for (j = 0; j < 2; j++)
        g2[256 + i][j] = g2[i][j]; 
      for (j = 0; j < 3; j++)
        g3[256 + i][j] = g3[i][j]; 
    } 
  }
  
  public static float[] findRange(Function1D f, float[] minmax) {
    if (minmax == null)
      minmax = new float[2]; 
    float min = 0.0F, max = 0.0F;
    float x;
    for (x = -100.0F; x < 100.0F; x = (float)(x + 1.27139D)) {
      float n = f.evaluate(x);
      min = Math.min(min, n);
      max = Math.max(max, n);
    } 
    minmax[0] = min;
    minmax[1] = max;
    return minmax;
  }
  
  public static float[] findRange(Function2D f, float[] minmax) {
    if (minmax == null)
      minmax = new float[2]; 
    float min = 0.0F, max = 0.0F;
    float y;
    for (y = -100.0F; y < 100.0F; y = (float)(y + 10.35173D)) {
      float x;
      for (x = -100.0F; x < 100.0F; x = (float)(x + 10.77139D)) {
        float n = f.evaluate(x, y);
        min = Math.min(min, n);
        max = Math.max(max, n);
      } 
    } 
    minmax[0] = min;
    minmax[1] = max;
    return minmax;
  }
}
