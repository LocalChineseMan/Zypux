package com.jhlabs.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ShatterFilter extends AbstractBufferedImageOp {
  private float centreX = 0.5F, centreY = 0.5F;
  
  private float distance;
  
  private float transition;
  
  private float rotation;
  
  private float zoom;
  
  private float startAlpha = 1.0F;
  
  private float endAlpha = 1.0F;
  
  private int iterations = 5;
  
  private int tile;
  
  public void setTransition(float transition) {
    this.transition = transition;
  }
  
  public float getTransition() {
    return this.transition;
  }
  
  public void setDistance(float distance) {
    this.distance = distance;
  }
  
  public float getDistance() {
    return this.distance;
  }
  
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }
  
  public float getRotation() {
    return this.rotation;
  }
  
  public void setZoom(float zoom) {
    this.zoom = zoom;
  }
  
  public float getZoom() {
    return this.zoom;
  }
  
  public void setStartAlpha(float startAlpha) {
    this.startAlpha = startAlpha;
  }
  
  public float getStartAlpha() {
    return this.startAlpha;
  }
  
  public void setEndAlpha(float endAlpha) {
    this.endAlpha = endAlpha;
  }
  
  public float getEndAlpha() {
    return this.endAlpha;
  }
  
  public void setCentreX(float centreX) {
    this.centreX = centreX;
  }
  
  public float getCentreX() {
    return this.centreX;
  }
  
  public void setCentreY(float centreY) {
    this.centreY = centreY;
  }
  
  public float getCentreY() {
    return this.centreY;
  }
  
  public void setCentre(Point2D centre) {
    this.centreX = (float)centre.getX();
    this.centreY = (float)centre.getY();
  }
  
  public Point2D getCentre() {
    return new Point2D.Float(this.centreX, this.centreY);
  }
  
  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
  
  public int getIterations() {
    return this.iterations;
  }
  
  public void setTile(int tile) {
    this.tile = tile;
  }
  
  public int getTile() {
    return this.tile;
  }
  
  static class Tile {
    float x;
    
    float y;
    
    float vx;
    
    float vy;
    
    float w;
    
    float h;
    
    float rotation;
    
    Shape shape;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    if (dst == null)
      dst = createCompatibleDestImage(src, null); 
    float width = src.getWidth();
    float height = src.getHeight();
    float cx = src.getWidth() * this.centreX;
    float cy = src.getHeight() * this.centreY;
    float imageRadius = (float)Math.sqrt((cx * cx + cy * cy));
    int numTiles = this.iterations * this.iterations;
    Tile[] shapes = new Tile[numTiles];
    float[] rx = new float[numTiles];
    float[] ry = new float[numTiles];
    float[] rz = new float[numTiles];
    Graphics2D g = dst.createGraphics();
    Random random = new Random(0L);
    float lastx = 0.0F, lasty = 0.0F;
    for (int y = 0; y < this.iterations; y++) {
      int y1 = (int)height * y / this.iterations;
      int y2 = (int)height * (y + 1) / this.iterations;
      for (int x = 0; x < this.iterations; x++) {
        int j = y * this.iterations + x;
        int x1 = (int)width * x / this.iterations;
        int x2 = (int)width * (x + 1) / this.iterations;
        rx[j] = this.tile * random.nextFloat();
        ry[j] = this.tile * random.nextFloat();
        rx[j] = 0.0F;
        ry[j] = 0.0F;
        rz[j] = this.tile * (2.0F * random.nextFloat() - 1.0F);
        Shape p = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        shapes[j] = new Tile();
        (shapes[j]).shape = p;
        (shapes[j]).x = (x1 + x2) * 0.5F;
        (shapes[j]).y = (y1 + y2) * 0.5F;
        (shapes[j]).vx = width - cx - x;
        (shapes[j]).vy = height - cy - y;
        (shapes[j]).w = (x2 - x1);
        (shapes[j]).h = (y2 - y1);
      } 
    } 
    for (int i = 0; i < numTiles; i++) {
      float h = i / numTiles;
      double angle = (h * 2.0F) * Math.PI;
      float x = this.transition * width * (float)Math.cos(angle);
      float f1 = this.transition * height * (float)Math.sin(angle);
      Tile tile = shapes[i];
      Rectangle r = tile.shape.getBounds();
      AffineTransform t = g.getTransform();
      x = tile.x + this.transition * tile.vx;
      f1 = tile.y + this.transition * tile.vy;
      g.translate(x, f1);
      g.rotate((this.transition * rz[i]));
      g.setColor(Color.getHSBColor(h, 1.0F, 1.0F));
      Shape clip = g.getClip();
      g.clip(tile.shape);
      g.drawImage(src, 0, 0, null);
      g.setClip(clip);
      g.setTransform(t);
    } 
    g.dispose();
    return dst;
  }
  
  public String toString() {
    return "Transition/Shatter...";
  }
}
