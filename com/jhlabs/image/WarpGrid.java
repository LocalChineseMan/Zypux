package com.jhlabs.image;

import java.io.Serializable;

public class WarpGrid implements Serializable {
  static final long serialVersionUID = 4312410199770201968L;
  
  public float[] xGrid = null;
  
  public float[] yGrid = null;
  
  public int rows;
  
  public int cols;
  
  private static final float m00 = -0.5F;
  
  private static final float m01 = 1.5F;
  
  private static final float m02 = -1.5F;
  
  private static final float m03 = 0.5F;
  
  private static final float m10 = 1.0F;
  
  private static final float m11 = -2.5F;
  
  private static final float m12 = 2.0F;
  
  private static final float m13 = -0.5F;
  
  private static final float m20 = -0.5F;
  
  private static final float m22 = 0.5F;
  
  private static final float m31 = 1.0F;
  
  public WarpGrid(int rows, int cols, int w, int h) {
    this.rows = rows;
    this.cols = cols;
    this.xGrid = new float[rows * cols];
    this.yGrid = new float[rows * cols];
    int index = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        this.xGrid[index] = col * (w - 1) / (cols - 1);
        this.yGrid[index] = row * (h - 1) / (rows - 1);
        index++;
      } 
    } 
  }
  
  public void addRow(int before) {
    int size = (this.rows + 1) * this.cols;
    float[] x = new float[size];
    float[] y = new float[size];
    this.rows++;
    int i = 0;
    int j = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        int k = j + col;
        int l = i + col;
        if (row == before) {
          x[k] = (this.xGrid[l] + this.xGrid[k]) / 2.0F;
          y[k] = (this.yGrid[l] + this.yGrid[k]) / 2.0F;
        } else {
          x[k] = this.xGrid[l];
          y[k] = this.yGrid[l];
        } 
      } 
      if (row != before - 1)
        i += this.cols; 
      j += this.cols;
    } 
    this.xGrid = x;
    this.yGrid = y;
  }
  
  public void addCol(int before) {
    int size = this.rows * (this.cols + 1);
    float[] x = new float[size];
    float[] y = new float[size];
    this.cols++;
    int i = 0;
    int j = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        if (col == before) {
          x[j] = (this.xGrid[i] + this.xGrid[i - 1]) / 2.0F;
          y[j] = (this.yGrid[i] + this.yGrid[i - 1]) / 2.0F;
        } else {
          x[j] = this.xGrid[i];
          y[j] = this.yGrid[i];
          i++;
        } 
        j++;
      } 
    } 
    this.xGrid = x;
    this.yGrid = y;
  }
  
  public void removeRow(int r) {
    int size = (this.rows - 1) * this.cols;
    float[] x = new float[size];
    float[] y = new float[size];
    this.rows--;
    int i = 0;
    int j = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        int k = j + col;
        int l = i + col;
        x[k] = this.xGrid[l];
        y[k] = this.yGrid[l];
      } 
      if (row == r - 1)
        i += this.cols; 
      i += this.cols;
      j += this.cols;
    } 
    this.xGrid = x;
    this.yGrid = y;
  }
  
  public void removeCol(int r) {
    int size = this.rows * (this.cols + 1);
    float[] x = new float[size];
    float[] y = new float[size];
    this.cols--;
    for (int row = 0; row < this.rows; row++) {
      int i = row * (this.cols + 1);
      int j = row * this.cols;
      for (int col = 0; col < this.cols; col++) {
        x[j] = this.xGrid[i];
        y[j] = this.yGrid[i];
        if (col == r - 1)
          i++; 
        i++;
        j++;
      } 
    } 
    this.xGrid = x;
    this.yGrid = y;
  }
  
  public void lerp(float t, WarpGrid destination, WarpGrid intermediate) {
    if (this.rows != destination.rows || this.cols != destination.cols)
      throw new IllegalArgumentException("source and destination are different sizes"); 
    if (this.rows != intermediate.rows || this.cols != intermediate.cols)
      throw new IllegalArgumentException("source and intermediate are different sizes"); 
    int index = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        intermediate.xGrid[index] = ImageMath.lerp(t, this.xGrid[index], destination.xGrid[index]);
        intermediate.yGrid[index] = ImageMath.lerp(t, this.yGrid[index], destination.yGrid[index]);
        index++;
      } 
    } 
  }
  
  public void warp(int[] inPixels, int cols, int rows, WarpGrid sourceGrid, WarpGrid destGrid, int[] outPixels) {
    try {
      if (sourceGrid.rows != destGrid.rows || sourceGrid.cols != destGrid.cols)
        throw new IllegalArgumentException("source and destination grids are different sizes"); 
      int size = Math.max(cols, rows);
      float[] xrow = new float[size];
      float[] yrow = new float[size];
      float[] scale = new float[size + 1];
      float[] interpolated = new float[size + 1];
      int gridCols = sourceGrid.cols;
      int gridRows = sourceGrid.rows;
      WarpGrid splines = new WarpGrid(rows, gridCols, 1, 1);
      int u;
      for (u = 0; u < gridCols; u++) {
        int i = u;
        for (int k = 0; k < gridRows; k++) {
          xrow[k] = sourceGrid.xGrid[i];
          yrow[k] = sourceGrid.yGrid[i];
          i += gridCols;
        } 
        interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
        i = u;
        for (int j = 0; j < rows; j++) {
          splines.xGrid[i] = interpolated[j];
          i += gridCols;
        } 
      } 
      for (u = 0; u < gridCols; u++) {
        int i = u;
        for (int k = 0; k < gridRows; k++) {
          xrow[k] = destGrid.xGrid[i];
          yrow[k] = destGrid.yGrid[i];
          i += gridCols;
        } 
        interpolateSpline(yrow, xrow, 0, gridRows, interpolated, 0, rows);
        i = u;
        for (int j = 0; j < rows; j++) {
          splines.yGrid[i] = interpolated[j];
          i += gridCols;
        } 
      } 
      int[] intermediate = new int[rows * cols];
      int offset = 0;
      for (int y = 0; y < rows; y++) {
        interpolateSpline(splines.xGrid, splines.yGrid, offset, gridCols, scale, 0, cols);
        scale[cols] = cols;
        ImageMath.resample(inPixels, intermediate, cols, y * cols, 1, scale);
        offset += gridCols;
      } 
      splines = new WarpGrid(gridRows, cols, 1, 1);
      offset = 0;
      int offset2 = 0;
      int v;
      for (v = 0; v < gridRows; v++) {
        interpolateSpline(sourceGrid.xGrid, sourceGrid.yGrid, offset, gridCols, splines.xGrid, offset2, cols);
        offset += gridCols;
        offset2 += cols;
      } 
      offset = 0;
      offset2 = 0;
      for (v = 0; v < gridRows; v++) {
        interpolateSpline(destGrid.xGrid, destGrid.yGrid, offset, gridCols, splines.yGrid, offset2, cols);
        offset += gridCols;
        offset2 += cols;
      } 
      for (int x = 0; x < cols; x++) {
        int i = x;
        for (v = 0; v < gridRows; v++) {
          xrow[v] = splines.xGrid[i];
          yrow[v] = splines.yGrid[i];
          i += cols;
        } 
        interpolateSpline(xrow, yrow, 0, gridRows, scale, 0, rows);
        scale[rows] = rows;
        ImageMath.resample(intermediate, outPixels, rows, x, cols, scale);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  protected void interpolateSpline(float[] xKnots, float[] yKnots, int offset, int length, float[] splineY, int splineOffset, int splineLength) {
    int index = offset;
    int end = offset + length - 1;
    float x0 = xKnots[index];
    float k2 = yKnots[index], k1 = k2, k0 = k1;
    float x1 = xKnots[index + 1];
    float k3 = yKnots[index + 1];
    for (int i = 0; i < splineLength; i++) {
      if (index <= end && i > xKnots[index]) {
        k0 = k1;
        k1 = k2;
        k2 = k3;
        x0 = xKnots[index];
        index++;
        if (index <= end)
          x1 = xKnots[index]; 
        if (index < end) {
          k3 = yKnots[index + 1];
        } else {
          k3 = k2;
        } 
      } 
      float t = (i - x0) / (x1 - x0);
      float c3 = -0.5F * k0 + 1.5F * k1 + -1.5F * k2 + 0.5F * k3;
      float c2 = 1.0F * k0 + -2.5F * k1 + 2.0F * k2 + -0.5F * k3;
      float c1 = -0.5F * k0 + 0.5F * k2;
      float c0 = 1.0F * k1;
      splineY[splineOffset + i] = ((c3 * t + c2) * t + c1) * t + c0;
    } 
  }
  
  protected void interpolateSpline2(float[] xKnots, float[] yKnots, int offset, float[] splineY, int splineOffset, int splineLength) {
    int index = offset;
    float leftX = xKnots[index];
    float leftY = yKnots[index];
    float rightX = xKnots[index + 1];
    float rightY = yKnots[index + 1];
    for (int i = 0; i < splineLength; i++) {
      if (i > xKnots[index]) {
        leftX = xKnots[index];
        leftY = yKnots[index];
        index++;
        rightX = xKnots[index];
        rightY = yKnots[index];
      } 
      float f = (i - leftX) / (rightX - leftX);
      splineY[splineOffset + i] = leftY + f * (rightY - leftY);
    } 
  }
}
