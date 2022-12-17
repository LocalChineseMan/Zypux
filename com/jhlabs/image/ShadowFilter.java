package com.jhlabs.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class ShadowFilter extends AbstractBufferedImageOp {
  static final long serialVersionUID = 6310370419462785691L;
  
  private float radius = 5.0F;
  
  private float angle = 4.712389F;
  
  private float distance = 5.0F;
  
  private float opacity = 0.5F;
  
  private boolean addMargins = false;
  
  private boolean shadowOnly = false;
  
  private int shadowColor = -16777216;
  
  public ShadowFilter() {}
  
  public ShadowFilter(float radius, float xOffset, float yOffset, float opacity) {
    this.radius = radius;
    this.angle = (float)Math.atan2(yOffset, xOffset);
    this.distance = (float)Math.sqrt((xOffset * xOffset + yOffset * yOffset));
    this.opacity = opacity;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setDistance(float distance) {
    this.distance = distance;
  }
  
  public float getDistance() {
    return this.distance;
  }
  
  public void setRadius(float radius) {
    this.radius = radius;
  }
  
  public float getRadius() {
    return this.radius;
  }
  
  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }
  
  public float getOpacity() {
    return this.opacity;
  }
  
  public void setShadowColor(int shadowColor) {
    this.shadowColor = shadowColor;
  }
  
  public int getShadowColor() {
    return this.shadowColor;
  }
  
  public void setAddMargins(boolean addMargins) {
    this.addMargins = addMargins;
  }
  
  public boolean getAddMargins() {
    return this.addMargins;
  }
  
  public void setShadowOnly(boolean shadowOnly) {
    this.shadowOnly = shadowOnly;
  }
  
  public boolean getShadowOnly() {
    return this.shadowOnly;
  }
  
  protected void transformSpace(Rectangle r) {
    if (this.addMargins) {
      float xOffset = this.distance * (float)Math.cos(this.angle);
      float yOffset = -this.distance * (float)Math.sin(this.angle);
      r.width += (int)(Math.abs(xOffset) + 2.0F * this.radius);
      r.height += (int)(Math.abs(yOffset) + 2.0F * this.radius);
    } 
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int width = src.getWidth();
    int height = src.getHeight();
    if (dst == null)
      if (this.addMargins) {
        ColorModel cm = src.getColorModel();
        dst = new BufferedImage(cm, cm.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), cm.isAlphaPremultiplied(), null);
      } else {
        dst = createCompatibleDestImage(src, null);
      }  
    float shadowR = (this.shadowColor >> 16 & 0xFF) / 255.0F;
    float shadowG = (this.shadowColor >> 8 & 0xFF) / 255.0F;
    float shadowB = (this.shadowColor & 0xFF) / 255.0F;
    float[][] extractAlpha = { { 0.0F, 0.0F, 0.0F, shadowR }, { 0.0F, 0.0F, 0.0F, shadowG }, { 0.0F, 0.0F, 0.0F, shadowB }, { 0.0F, 0.0F, 0.0F, this.opacity } };
    BufferedImage shadow = new BufferedImage(width, height, 2);
    (new BandCombineOp(extractAlpha, null)).filter(src.getRaster(), shadow.getRaster());
    shadow = (new GaussianFilter(this.radius)).filter(shadow, null);
    float xOffset = this.distance * (float)Math.cos(this.angle);
    float yOffset = -this.distance * (float)Math.sin(this.angle);
    Graphics2D g = dst.createGraphics();
    g.setComposite(AlphaComposite.getInstance(3, this.opacity));
    if (this.addMargins) {
      float radius2 = this.radius / 2.0F;
      float topShadow = Math.max(0.0F, this.radius - yOffset);
      float leftShadow = Math.max(0.0F, this.radius - xOffset);
      g.translate(topShadow, leftShadow);
    } 
    g.drawRenderedImage(shadow, AffineTransform.getTranslateInstance(xOffset, yOffset));
    if (!this.shadowOnly) {
      g.setComposite(AlphaComposite.SrcOver);
      g.drawRenderedImage(src, null);
    } 
    g.dispose();
    return dst;
  }
  
  public String toString() {
    return "Stylize/Drop Shadow...";
  }
}
