package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.Serializable;

public class TileImageFilter extends AbstractBufferedImageOp implements Serializable {
  static final long serialVersionUID = 4926390225069192478L;
  
  public static final int FLIP_NONE = 0;
  
  public static final int FLIP_H = 1;
  
  public static final int FLIP_V = 2;
  
  public static final int FLIP_HV = 3;
  
  public static final int FLIP_180 = 4;
  
  private int width;
  
  private int height;
  
  private int tileWidth;
  
  private int tileHeight;
  
  private int[][] symmetryMatrix;
  
  private int symmetryRows;
  
  private int symmetryCols;
  
  public TileImageFilter() {
    this(32, 32);
  }
  
  public TileImageFilter(int width, int height) {
    this.symmetryMatrix = (int[][])null;
    this.symmetryRows = 2;
    this.symmetryCols = 2;
    this.width = width;
    this.height = height;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int tileWidth = src.getWidth();
    int tileHeight = src.getHeight();
    if (dst == null) {
      ColorModel dstCM = src.getColorModel();
      dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
    } 
    Graphics2D g = dst.createGraphics();
    int y;
    for (y = 0; y < this.height; y += tileHeight) {
      int x;
      for (x = 0; x < this.width; x += tileWidth)
        g.drawImage(src, null, x, y); 
    } 
    g.dispose();
    return dst;
  }
  
  public void setSymmetryMatrix(int[][] symmetryMatrix) {
    this.symmetryMatrix = symmetryMatrix;
    this.symmetryRows = symmetryMatrix.length;
    this.symmetryCols = (symmetryMatrix[0]).length;
  }
  
  public int[][] getSymmetryMatrix() {
    return this.symmetryMatrix;
  }
  
  public String toString() {
    return "Tile";
  }
}
