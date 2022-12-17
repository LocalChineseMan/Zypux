package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class CurlFilter extends TransformFilter {
  private float angle = 0.0F;
  
  private float transition = 0.0F;
  
  private float width;
  
  private float height;
  
  private float radius;
  
  public CurlFilter() {
    setEdgeAction(0);
  }
  
  public void setTransition(float transition) {
    this.transition = transition;
  }
  
  public float getTransition() {
    return this.transition;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setRadius(float radius) {
    this.radius = radius;
  }
  
  public float getRadius() {
    return this.radius;
  }
  
  static class Sampler {
    private int edgeAction;
    
    private int width;
    
    private int height;
    
    private int[] inPixels;
    
    public Sampler(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int type = image.getType();
      this.inPixels = ImageUtils.getRGB(image, 0, 0, width, height, null);
    }
    
    public int sample(float x, float y) {
      int nw, ne, sw, se, srcX = (int)Math.floor(x);
      int srcY = (int)Math.floor(y);
      float xWeight = x - srcX;
      float yWeight = y - srcY;
      if (srcX >= 0 && srcX < this.width - 1 && srcY >= 0 && srcY < this.height - 1) {
        int i = this.width * srcY + srcX;
        nw = this.inPixels[i];
        ne = this.inPixels[i + 1];
        sw = this.inPixels[i + this.width];
        se = this.inPixels[i + this.width + 1];
      } else {
        nw = getPixel(this.inPixels, srcX, srcY, this.width, this.height);
        ne = getPixel(this.inPixels, srcX + 1, srcY, this.width, this.height);
        sw = getPixel(this.inPixels, srcX, srcY + 1, this.width, this.height);
        se = getPixel(this.inPixels, srcX + 1, srcY + 1, this.width, this.height);
      } 
      return ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
    }
    
    private final int getPixel(int[] pixels, int x, int y, int width, int height) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
        switch (this.edgeAction) {
          default:
            return 0;
          case 2:
            return pixels[ImageMath.mod(y, height) * width + ImageMath.mod(x, width)];
          case 1:
            break;
        } 
        return pixels[ImageMath.clamp(y, 0, height - 1) * width + ImageMath.clamp(x, 0, width - 1)];
      } 
      return pixels[y * width + x];
    }
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int width = src.getWidth();
    int height = src.getHeight();
    this.width = src.getWidth();
    this.height = src.getHeight();
    int type = src.getType();
    this.originalSpace = new Rectangle(0, 0, width, height);
    this.transformedSpace = new Rectangle(0, 0, width, height);
    transformSpace(this.transformedSpace);
    if (dst == null) {
      ColorModel dstCM = src.getColorModel();
      dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.transformedSpace.width, this.transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
    } 
    WritableRaster dstRaster = dst.getRaster();
    int[] inPixels = getRGB(src, 0, 0, width, height, null);
    if (this.interpolation == 0)
      return filterPixelsNN(dst, width, height, inPixels, this.transformedSpace); 
    int srcWidth = width;
    int srcHeight = height;
    int srcWidth1 = width - 1;
    int srcHeight1 = height - 1;
    int outWidth = this.transformedSpace.width;
    int outHeight = this.transformedSpace.height;
    int index = 0;
    int[] outPixels = new int[outWidth];
    int outX = this.transformedSpace.x;
    int outY = this.transformedSpace.y;
    float[] out = new float[4];
    for (int y = 0; y < outHeight; y++) {
      for (int x = 0; x < outWidth; x++) {
        int nw, ne, sw, se;
        transformInverse(outX + x, outY + y, out);
        int srcX = (int)Math.floor(out[0]);
        int srcY = (int)Math.floor(out[1]);
        float xWeight = out[0] - srcX;
        float yWeight = out[1] - srcY;
        if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
          int i = srcWidth * srcY + srcX;
          nw = inPixels[i];
          ne = inPixels[i + 1];
          sw = inPixels[i + srcWidth];
          se = inPixels[i + srcWidth + 1];
        } else {
          nw = getPixel(inPixels, srcX, srcY, srcWidth, srcHeight);
          ne = getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
          sw = getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
          se = getPixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
        } 
        int rgb = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        float shade = out[2];
        r = (int)(r * shade);
        g = (int)(g * shade);
        b = (int)(b * shade);
        rgb = rgb & 0xFF000000 | r << 16 | g << 8 | b;
        if (out[3] != 0.0F) {
          outPixels[x] = PixelUtils.combinePixels(rgb, inPixels[srcWidth * y + x], 1);
        } else {
          outPixels[x] = rgb;
        } 
      } 
      setRGB(dst, 0, y, this.transformedSpace.width, 1, outPixels);
    } 
    return dst;
  }
  
  private final int getPixel(int[] pixels, int x, int y, int width, int height) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
      switch (this.edgeAction) {
        default:
          return 0;
        case 2:
          return pixels[ImageMath.mod(y, height) * width + ImageMath.mod(x, width)];
        case 1:
          break;
      } 
      return pixels[ImageMath.clamp(y, 0, height - 1) * width + ImageMath.clamp(x, 0, width - 1)];
    } 
    return pixels[y * width + x];
  }
  
  protected void transformInverse(int x, int y, float[] out) {
    float t = this.transition;
    float px = x, py = y;
    float s = (float)Math.sin(this.angle);
    float c = (float)Math.cos(this.angle);
    float tx = t * this.width;
    tx = t * (float)Math.sqrt((this.width * this.width + this.height * this.height));
    float xoffset = (c < 0.0F) ? this.width : 0.0F;
    float yoffset = (s < 0.0F) ? this.height : 0.0F;
    px -= xoffset;
    py -= yoffset;
    float qx = px * c + py * s;
    float qy = -px * s + py * c;
    boolean outside = (qx < tx);
    boolean unfolded = (qx > tx * 2.0F);
    boolean oncurl = (!outside && !unfolded);
    qx = (qx > tx * 2.0F) ? qx : (2.0F * tx - qx);
    px = qx * c - qy * s;
    py = qx * s + qy * c;
    px += xoffset;
    py += yoffset;
    boolean offpage = (px < 0.0F || py < 0.0F || px >= this.width || py >= this.height);
    if (offpage && oncurl) {
      px = x;
      py = y;
    } 
    float shade = (!offpage && oncurl) ? (1.9F * (1.0F - (float)Math.cos(Math.exp(((qx - tx) / this.radius))))) : 0.0F;
    out[2] = 1.0F - shade;
    if (outside) {
      out[1] = -1.0F;
      out[0] = -1.0F;
    } else {
      out[0] = px;
      out[1] = py;
    } 
    out[3] = (!offpage && oncurl) ? 1.0F : 0.0F;
  }
  
  public String toString() {
    return "Distort/Curl...";
  }
}
