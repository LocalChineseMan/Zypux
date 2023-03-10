package com.jhlabs.math;

public class FFT {
  protected float[] w1;
  
  protected float[] w2;
  
  protected float[] w3;
  
  public FFT(int logN) {
    this.w1 = new float[logN];
    this.w2 = new float[logN];
    this.w3 = new float[logN];
    int N = 1;
    for (int k = 0; k < logN; k++) {
      N <<= 1;
      double angle = -6.283185307179586D / N;
      this.w1[k] = (float)Math.sin(0.5D * angle);
      this.w2[k] = -2.0F * this.w1[k] * this.w1[k];
      this.w3[k] = (float)Math.sin(angle);
    } 
  }
  
  private void scramble(int n, float[] real, float[] imag) {
    int j = 0;
    for (int i = 0; i < n; i++) {
      if (i > j) {
        float t = real[j];
        real[j] = real[i];
        real[i] = t;
        t = imag[j];
        imag[j] = imag[i];
        imag[i] = t;
      } 
      int m = n >> 1;
      while (j >= m && m >= 2) {
        j -= m;
        m >>= 1;
      } 
      j += m;
    } 
  }
  
  private void butterflies(int n, int logN, int direction, float[] real, float[] imag) {
    int N = 1;
    for (int k = 0; k < logN; k++) {
      int half_N = N;
      N <<= 1;
      float wt = direction * this.w1[k];
      float wp_re = this.w2[k];
      float wp_im = direction * this.w3[k];
      float w_re = 1.0F;
      float w_im = 0.0F;
      for (int offset = 0; offset < half_N; offset++) {
        int i;
        for (i = offset; i < n; i += N) {
          int j = i + half_N;
          float re = real[j];
          float im = imag[j];
          float temp_re = w_re * re - w_im * im;
          float temp_im = w_im * re + w_re * im;
          real[j] = real[i] - temp_re;
          real[i] = real[i] + temp_re;
          imag[j] = imag[i] - temp_im;
          imag[i] = imag[i] + temp_im;
        } 
        wt = w_re;
        w_re = wt * wp_re - w_im * wp_im + w_re;
        w_im = w_im * wp_re + wt * wp_im + w_im;
      } 
    } 
    if (direction == -1) {
      float nr = 1.0F / n;
      for (int i = 0; i < n; i++) {
        real[i] = real[i] * nr;
        imag[i] = imag[i] * nr;
      } 
    } 
  }
  
  public void transform1D(float[] real, float[] imag, int logN, int n, boolean forward) {
    scramble(n, real, imag);
    butterflies(n, logN, forward ? 1 : -1, real, imag);
  }
  
  public void transform2D(float[] real, float[] imag, int cols, int rows, boolean forward) {
    int log2cols = log2(cols);
    int log2rows = log2(rows);
    int n = Math.max(rows, cols);
    float[] rtemp = new float[n];
    float[] itemp = new float[n];
    for (int y = 0; y < rows; y++) {
      int offset = y * cols;
      System.arraycopy(real, offset, rtemp, 0, cols);
      System.arraycopy(imag, offset, itemp, 0, cols);
      transform1D(rtemp, itemp, log2cols, cols, forward);
      System.arraycopy(rtemp, 0, real, offset, cols);
      System.arraycopy(itemp, 0, imag, offset, cols);
    } 
    for (int x = 0; x < cols; x++) {
      int index = x;
      int i;
      for (i = 0; i < rows; i++) {
        rtemp[i] = real[index];
        itemp[i] = imag[index];
        index += cols;
      } 
      transform1D(rtemp, itemp, log2rows, rows, forward);
      index = x;
      for (i = 0; i < rows; i++) {
        real[index] = rtemp[i];
        imag[index] = itemp[i];
        index += cols;
      } 
    } 
  }
  
  private int log2(int n) {
    int m = 1;
    int log2n = 0;
    while (m < n) {
      m *= 2;
      log2n++;
    } 
    return (m == n) ? log2n : -1;
  }
}
