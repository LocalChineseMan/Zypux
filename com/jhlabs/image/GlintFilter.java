package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class GlintFilter extends AbstractBufferedImageOp {
  private float threshold = 1.0F;
  
  private int length = 5;
  
  private float blur = 0.0F;
  
  private float amount = 0.1F;
  
  private boolean glintOnly = false;
  
  private Colormap colormap = new LinearColormap(-1, -16777216);
  
  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }
  
  public float getThreshold() {
    return this.threshold;
  }
  
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  public float getAmount() {
    return this.amount;
  }
  
  public void setLength(int length) {
    this.length = length;
  }
  
  public int getLength() {
    return this.length;
  }
  
  public void setBlur(float blur) {
    this.blur = blur;
  }
  
  public float getBlur() {
    return this.blur;
  }
  
  public void setGlintOnly(boolean glintOnly) {
    this.glintOnly = glintOnly;
  }
  
  public boolean getGlintOnly() {
    return this.glintOnly;
  }
  
  public void setColormap(Colormap colormap) {
    this.colormap = colormap;
  }
  
  public Colormap getColormap() {
    return this.colormap;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    int dstPixels[], width = src.getWidth();
    int height = src.getHeight();
    int[] pixels = new int[width];
    int length2 = (int)(this.length / 1.414F);
    int[] colors = new int[this.length + 1];
    int[] colors2 = new int[length2 + 1];
    if (this.colormap != null) {
      int j;
      for (j = 0; j <= this.length; j++) {
        int argb = this.colormap.getColor(j / this.length);
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        argb = argb & 0xFF000000 | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b);
        colors[j] = argb;
      } 
      for (j = 0; j <= length2; j++) {
        int argb = this.colormap.getColor(j / length2);
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        argb = argb & 0xFF000000 | (int)(this.amount * r) << 16 | (int)(this.amount * g) << 8 | (int)(this.amount * b);
        colors2[j] = argb;
      } 
    } 
    BufferedImage mask = new BufferedImage(width, height, 2);
    int threshold3 = (int)(this.threshold * 3.0F * 255.0F);
    for (int y = 0; y < height; y++) {
      getRGB(src, 0, y, width, 1, pixels);
      for (int x = 0; x < width; x++) {
        int rgb = pixels[x];
        int a = rgb & 0xFF000000;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        int l = r + g + b;
        if (l < threshold3) {
          pixels[x] = -16777216;
        } else {
          l /= 3;
          pixels[x] = a | l << 16 | l << 8 | l;
        } 
      } 
      setRGB(mask, 0, y, width, 1, pixels);
    } 
    if (this.blur != 0.0F)
      mask = (new GaussianFilter(this.blur)).filter(mask, null); 
    if (dst == null)
      dst = createCompatibleDestImage(src, null); 
    if (this.glintOnly) {
      dstPixels = new int[width * height];
    } else {
      dstPixels = getRGB(src, 0, 0, width, height, null);
    } 
    for (int i = 0; i < height; i++) {
      int index = i * width;
      getRGB(mask, 0, i, width, 1, pixels);
      int ymin = Math.max(i - this.length, 0) - i;
      int ymax = Math.min(i + this.length, height - 1) - i;
      int ymin2 = Math.max(i - length2, 0) - i;
      int ymax2 = Math.min(i + length2, height - 1) - i;
      for (int x = 0; x < width; x++) {
        if ((pixels[x] & 0xFF) > this.threshold * 255.0F) {
          int xmin = Math.max(x - this.length, 0) - x;
          int xmax = Math.min(x + this.length, width - 1) - x;
          int xmin2 = Math.max(x - length2, 0) - x;
          int xmax2 = Math.min(x + length2, width - 1) - x;
          int m, k;
          for (m = 0, k = 0; m <= xmax; m++, k++)
            dstPixels[index + m] = PixelUtils.combinePixels(dstPixels[index + m], colors[k], 4); 
          for (m = -1, k = 1; m >= xmin; m--, k++)
            dstPixels[index + m] = PixelUtils.combinePixels(dstPixels[index + m], colors[k], 4); 
          int j, n;
          for (m = 1, j = index + width, n = 0; m <= ymax; m++, j += width, n++)
            dstPixels[j] = PixelUtils.combinePixels(dstPixels[j], colors[n], 4); 
          for (m = -1, j = index - width, n = 0; m >= ymin; m--, j -= width, n++)
            dstPixels[j] = PixelUtils.combinePixels(dstPixels[j], colors[n], 4); 
          int xymin = Math.max(xmin2, ymin2);
          int xymax = Math.min(xmax2, ymax2);
          int count = Math.min(xmax2, ymax2);
          int i1, i2, i3;
          for (i1 = 1, i2 = index + width + 1, i3 = 0; i1 <= count; i1++, i2 += width + 1, i3++)
            dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4); 
          count = Math.min(-xmin2, -ymin2);
          for (i1 = 1, i2 = index - width - 1, i3 = 0; i1 <= count; i1++, i2 -= width + 1, i3++)
            dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4); 
          count = Math.min(xmax2, -ymin2);
          for (i1 = 1, i2 = index - width + 1, i3 = 0; i1 <= count; i1++, i2 += -width + 1, i3++)
            dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4); 
          count = Math.min(-xmin2, ymax2);
          for (i1 = 1, i2 = index + width - 1, i3 = 0; i1 <= count; i1++, i2 += width - 1, i3++)
            dstPixels[i2] = PixelUtils.combinePixels(dstPixels[i2], colors2[i3], 4); 
        } 
        index++;
      } 
    } 
    setRGB(dst, 0, 0, width, height, dstPixels);
    return dst;
  }
  
  public String toString() {
    return "Effects/Glint...";
  }
}
