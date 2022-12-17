package com.jhlabs.image;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class SmearFilter extends WholeImageFilter implements Serializable {
  static final long serialVersionUID = 6491871753122667752L;
  
  public static final int CROSSES = 0;
  
  public static final int LINES = 1;
  
  public static final int CIRCLES = 2;
  
  public static final int SQUARES = 3;
  
  private Colormap colormap = new LinearColormap();
  
  private float angle = 0.0F;
  
  private float density = 0.5F;
  
  private float scatter = 0.0F;
  
  private int distance = 8;
  
  private Random randomGenerator;
  
  private long seed = 567L;
  
  private int shape = 1;
  
  private float mix = 0.5F;
  
  private int fadeout = 0;
  
  private boolean background = false;
  
  public SmearFilter() {
    this.randomGenerator = new Random();
  }
  
  public void setShape(int shape) {
    this.shape = shape;
  }
  
  public int getShape() {
    return this.shape;
  }
  
  public void setDistance(int distance) {
    this.distance = distance;
  }
  
  public int getDistance() {
    return this.distance;
  }
  
  public void setDensity(float density) {
    this.density = density;
  }
  
  public float getDensity() {
    return this.density;
  }
  
  public void setScatter(float scatter) {
    this.scatter = scatter;
  }
  
  public float getScatter() {
    return this.scatter;
  }
  
  public void setAngle(float angle) {
    this.angle = angle;
  }
  
  public float getAngle() {
    return this.angle;
  }
  
  public void setMix(float mix) {
    this.mix = mix;
  }
  
  public float getMix() {
    return this.mix;
  }
  
  public void setFadeout(int fadeout) {
    this.fadeout = fadeout;
  }
  
  public int getFadeout() {
    return this.fadeout;
  }
  
  public void setBackground(boolean background) {
    this.background = background;
  }
  
  public boolean getBackground() {
    return this.background;
  }
  
  public void randomize() {
    this.seed = (new Date()).getTime();
  }
  
  private float random(float low, float high) {
    return low + (high - low) * this.randomGenerator.nextFloat();
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int numShapes, radius, radius2, outPixels[] = new int[width * height];
    this.randomGenerator.setSeed(this.seed);
    float sinAngle = (float)Math.sin(this.angle);
    float cosAngle = (float)Math.cos(this.angle);
    int i = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        outPixels[i] = this.background ? -1 : inPixels[i];
        i++;
      } 
    } 
    switch (this.shape) {
      case 0:
        numShapes = (int)(2.0F * this.density * width * height / (this.distance + 1));
        for (i = 0; i < numShapes; i++) {
          int x = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
          int j = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
          int length = this.randomGenerator.nextInt() % this.distance + 1;
          int rgb = inPixels[j * width + x];
          for (int x1 = x - length; x1 < x + length + 1; x1++) {
            if (x1 >= 0 && x1 < width) {
              int rgb2 = this.background ? -1 : outPixels[j * width + x1];
              outPixels[j * width + x1] = ImageMath.mixColors(this.mix, rgb2, rgb);
            } 
          } 
          for (int y1 = j - length; y1 < j + length + 1; y1++) {
            if (y1 >= 0 && y1 < height) {
              int rgb2 = this.background ? -1 : outPixels[y1 * width + x];
              outPixels[y1 * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
            } 
          } 
        } 
        break;
      case 1:
        numShapes = (int)(2.0F * this.density * width * height / 2.0F);
        for (i = 0; i < numShapes; i++) {
          int ddx, ddy, sx = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
          int sy = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
          int rgb = inPixels[sy * width + sx];
          int length = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % this.distance;
          int dx = (int)(length * cosAngle);
          int dy = (int)(length * sinAngle);
          int x0 = sx - dx;
          int y0 = sy - dy;
          int x1 = sx + dx;
          int y1 = sy + dy;
          if (x1 < x0) {
            ddx = -1;
          } else {
            ddx = 1;
          } 
          if (y1 < y0) {
            ddy = -1;
          } else {
            ddy = 1;
          } 
          dx = x1 - x0;
          dy = y1 - y0;
          dx = Math.abs(dx);
          dy = Math.abs(dy);
          int x = x0;
          int j = y0;
          if (x < width && x >= 0 && j < height && j >= 0) {
            int rgb2 = this.background ? -1 : outPixels[j * width + x];
            outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
          } 
          if (Math.abs(dx) > Math.abs(dy)) {
            int d = 2 * dy - dx;
            int incrE = 2 * dy;
            int incrNE = 2 * (dy - dx);
            while (x != x1) {
              if (d <= 0) {
                d += incrE;
              } else {
                d += incrNE;
                j += ddy;
              } 
              x += ddx;
              if (x < width && x >= 0 && j < height && j >= 0) {
                int rgb2 = this.background ? -1 : outPixels[j * width + x];
                outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
              } 
            } 
          } else {
            int d = 2 * dx - dy;
            int incrE = 2 * dx;
            int incrNE = 2 * (dx - dy);
            while (j != y1) {
              if (d <= 0) {
                d += incrE;
              } else {
                d += incrNE;
                x += ddx;
              } 
              j += ddy;
              if (x < width && x >= 0 && j < height && j >= 0) {
                int rgb2 = this.background ? -1 : outPixels[j * width + x];
                outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
              } 
            } 
          } 
        } 
        break;
      case 2:
      case 3:
        radius = this.distance + 1;
        radius2 = radius * radius;
        numShapes = (int)(2.0F * this.density * width * height / radius);
        for (i = 0; i < numShapes; i++) {
          int sx = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % width;
          int sy = (this.randomGenerator.nextInt() & Integer.MAX_VALUE) % height;
          int rgb = inPixels[sy * width + sx];
          for (int x = sx - radius; x < sx + radius + 1; x++) {
            for (int j = sy - radius; j < sy + radius + 1; j++) {
              int f;
              if (this.shape == 2) {
                f = (x - sx) * (x - sx) + (j - sy) * (j - sy);
              } else {
                f = 0;
              } 
              if (x >= 0 && x < width && j >= 0 && j < height && f <= radius2) {
                int rgb2 = this.background ? -1 : outPixels[j * width + x];
                outPixels[j * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
              } 
            } 
          } 
        } 
        break;
    } 
    return outPixels;
  }
  
  public String toString() {
    return "Effects/Smear...";
  }
}
