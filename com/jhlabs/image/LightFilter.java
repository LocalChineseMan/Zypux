package com.jhlabs.image;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.ImageFunction2D;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Tuple3f;
import com.jhlabs.vecmath.Tuple4f;
import com.jhlabs.vecmath.Vector3f;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.Serializable;
import java.util.Vector;

public class LightFilter extends WholeImageFilter implements Serializable {
  public static final int COLORS_FROM_IMAGE = 0;
  
  public static final int COLORS_CONSTANT = 1;
  
  public static final int BUMPS_FROM_IMAGE = 0;
  
  public static final int BUMPS_FROM_IMAGE_ALPHA = 1;
  
  public static final int BUMPS_FROM_MAP = 2;
  
  public static final int BUMPS_FROM_BEVEL = 3;
  
  private float bumpHeight;
  
  private float bumpSoftness;
  
  private int bumpShape;
  
  private float viewDistance = 10000.0F;
  
  Material material;
  
  private Vector lights;
  
  private int colorSource = 0;
  
  private int bumpSource = 0;
  
  private Function2D bumpFunction;
  
  private Image environmentMap;
  
  private int[] envPixels;
  
  private int envWidth = 1;
  
  private int envHeight = 1;
  
  private Vector3f l;
  
  private Vector3f v;
  
  private Vector3f n;
  
  private Color4f shadedColor;
  
  private Color4f diffuse_color;
  
  private Color4f specular_color;
  
  private Vector3f tmpv;
  
  private Vector3f tmpv2;
  
  protected static final float r255 = 0.003921569F;
  
  public static final int AMBIENT = 0;
  
  public static final int DISTANT = 1;
  
  public static final int POINT = 2;
  
  public static final int SPOT = 3;
  
  public LightFilter() {
    this.lights = new Vector();
    addLight(new DistantLight(this));
    this.bumpHeight = 1.0F;
    this.bumpSoftness = 5.0F;
    this.bumpShape = 0;
    this.material = new Material();
    this.l = new Vector3f();
    this.v = new Vector3f();
    this.n = new Vector3f();
    this.shadedColor = new Color4f();
    this.diffuse_color = new Color4f();
    this.specular_color = new Color4f();
    this.tmpv = new Vector3f();
    this.tmpv2 = new Vector3f();
  }
  
  public void setBumpFunction(Function2D bumpFunction) {
    this.bumpFunction = bumpFunction;
  }
  
  public Function2D getBumpFunction() {
    return this.bumpFunction;
  }
  
  public void setBumpHeight(float bumpHeight) {
    this.bumpHeight = bumpHeight;
  }
  
  public float getBumpHeight() {
    return this.bumpHeight;
  }
  
  public void setBumpSoftness(float bumpSoftness) {
    this.bumpSoftness = bumpSoftness;
  }
  
  public float getBumpSoftness() {
    return this.bumpSoftness;
  }
  
  public void setBumpShape(int bumpShape) {
    this.bumpShape = bumpShape;
  }
  
  public int getBumpShape() {
    return this.bumpShape;
  }
  
  public void setViewDistance(float viewDistance) {
    this.viewDistance = viewDistance;
  }
  
  public float getViewDistance() {
    return this.viewDistance;
  }
  
  public void setEnvironmentMap(BufferedImage environmentMap) {
    this.environmentMap = environmentMap;
    if (environmentMap != null) {
      this.envWidth = environmentMap.getWidth();
      this.envHeight = environmentMap.getHeight();
      this.envPixels = getRGB(environmentMap, 0, 0, this.envWidth, this.envHeight, null);
    } else {
      this.envWidth = this.envHeight = 1;
      this.envPixels = null;
    } 
  }
  
  public Image getEnvironmentMap() {
    return this.environmentMap;
  }
  
  public void setColorSource(int colorSource) {
    this.colorSource = colorSource;
  }
  
  public int getColorSource() {
    return this.colorSource;
  }
  
  public void setBumpSource(int bumpSource) {
    this.bumpSource = bumpSource;
  }
  
  public int getBumpSource() {
    return this.bumpSource;
  }
  
  public void setDiffuseColor(int diffuseColor) {
    this.material.diffuseColor = diffuseColor;
  }
  
  public int getDiffuseColor() {
    return this.material.diffuseColor;
  }
  
  public void addLight(Light light) {
    this.lights.addElement(light);
  }
  
  public void removeLight(Light light) {
    this.lights.removeElement(light);
  }
  
  public Vector getLights() {
    return this.lights;
  }
  
  protected void setFromRGB(Color4f c, int argb) {
    c.set((argb >> 16 & 0xFF) * 0.003921569F, (argb >> 8 & 0xFF) * 0.003921569F, (argb & 0xFF) * 0.003921569F, (argb >> 24 & 0xFF) * 0.003921569F);
  }
  
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    ImageFunction2D imageFunction2D;
    int index = 0;
    int[] outPixels = new int[width * height];
    float width45 = Math.abs(6.0F * this.bumpHeight);
    boolean invertBumps = (this.bumpHeight < 0.0F);
    Vector3f position = new Vector3f(0.0F, 0.0F, 0.0F);
    Vector3f viewpoint = new Vector3f(width / 2.0F, height / 2.0F, this.viewDistance);
    Vector3f normal = new Vector3f();
    Color4f envColor = new Color4f();
    Color4f diffuseColor = new Color4f(new Color(this.material.diffuseColor));
    Color4f specularColor = new Color4f(new Color(this.material.specularColor));
    Function2D bump = this.bumpFunction;
    if (this.bumpSource == 0 || this.bumpSource == 1 || this.bumpSource == 2 || bump == null)
      if (this.bumpSoftness != 0.0F) {
        int bumpWidth = width;
        int bumpHeight = height;
        int[] bumpPixels = inPixels;
        if (this.bumpSource == 2 && this.bumpFunction instanceof ImageFunction2D) {
          ImageFunction2D if2d = (ImageFunction2D)this.bumpFunction;
          bumpWidth = if2d.getWidth();
          bumpHeight = if2d.getHeight();
          bumpPixels = if2d.getPixels();
        } 
        int[] tmpPixels = new int[bumpWidth * bumpHeight];
        int[] softPixels = new int[bumpWidth * bumpHeight];
        Kernel kernel = GaussianFilter.makeKernel(this.bumpSoftness);
        GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, ConvolveFilter.WRAP_EDGES);
        GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, ConvolveFilter.WRAP_EDGES);
        imageFunction2D = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, 1, (this.bumpSource == 1));
        ImageFunction2D imageFunction2D1 = imageFunction2D;
        if (this.bumpShape != 0)
          Function2D function2D = new Function2D(this, (Function2D)imageFunction2D1) {
              private Function2D original;
              
              private final Function2D val$bbump;
              
              private final LightFilter this$0;
              
              public float evaluate(float x, float y) {
                float v = this.original.evaluate(x, y);
                switch (this.this$0.bumpShape) {
                  case 1:
                    v = (v > 0.5F) ? 0.5F : v;
                    break;
                  case 2:
                    v = (v < 0.5F) ? 0.5F : v;
                    break;
                  case 3:
                    v = ImageMath.triangle(v);
                    break;
                  case 4:
                    v = ImageMath.circleDown(v);
                    break;
                  case 5:
                    v = ImageMath.gain(v, 0.75F);
                    break;
                } 
                return v;
              }
            }; 
      } else if (this.bumpSource != 2) {
        imageFunction2D = new ImageFunction2D(inPixels, width, height, 1, (this.bumpSource == 1));
      }  
    float reflectivity = this.material.reflectivity;
    float areflectivity = 1.0F - reflectivity;
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    Vector3f n = new Vector3f();
    Light[] lightsArray = new Light[this.lights.size()];
    this.lights.copyInto((Object[])lightsArray);
    for (int i = 0; i < lightsArray.length; i++)
      lightsArray[i].prepare(width, height); 
    float[][] heightWindow = new float[3][width];
    for (int x = 0; x < width; x++)
      heightWindow[1][x] = width45 * imageFunction2D.evaluate(x, 0.0F); 
    for (int y = 0; y < height; y++) {
      boolean y0 = (y > 0);
      boolean y1 = (y < height - 1);
      ((Tuple3f)position).y = y;
      int j;
      for (j = 0; j < width; j++)
        heightWindow[2][j] = width45 * imageFunction2D.evaluate(j, (y + 1)); 
      for (j = 0; j < width; j++) {
        boolean x0 = (j > 0);
        boolean x1 = (j < width - 1);
        if (this.bumpSource != 3) {
          int count = 0;
          ((Tuple3f)normal).x = ((Tuple3f)normal).y = ((Tuple3f)normal).z = 0.0F;
          float m0 = heightWindow[1][j];
          float m1 = x0 ? (heightWindow[1][j - 1] - m0) : 0.0F;
          float m2 = y0 ? (heightWindow[0][j] - m0) : 0.0F;
          float m3 = x1 ? (heightWindow[1][j + 1] - m0) : 0.0F;
          float m4 = y1 ? (heightWindow[2][j] - m0) : 0.0F;
          if (x0 && y1) {
            ((Tuple3f)v1).x = -1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m1;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = 1.0F;
            ((Tuple3f)v2).z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (x0 && y0) {
            ((Tuple3f)v1).x = -1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m1;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = -1.0F;
            ((Tuple3f)v2).z = m2;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (y0 && x1) {
            ((Tuple3f)v1).x = 0.0F;
            ((Tuple3f)v1).y = -1.0F;
            ((Tuple3f)v1).z = m2;
            ((Tuple3f)v2).x = 1.0F;
            ((Tuple3f)v2).y = 0.0F;
            ((Tuple3f)v2).z = m3;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          if (x1 && y1) {
            ((Tuple3f)v1).x = 1.0F;
            ((Tuple3f)v1).y = 0.0F;
            ((Tuple3f)v1).z = m3;
            ((Tuple3f)v2).x = 0.0F;
            ((Tuple3f)v2).y = 1.0F;
            ((Tuple3f)v2).z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (((Tuple3f)n).z < 0.0D)
              ((Tuple3f)n).z = -((Tuple3f)n).z; 
            normal.add((Tuple3f)n);
            count++;
          } 
          ((Tuple3f)normal).x /= count;
          ((Tuple3f)normal).y /= count;
          ((Tuple3f)normal).z /= count;
        } 
        if (invertBumps) {
          ((Tuple3f)normal).x = -((Tuple3f)normal).x;
          ((Tuple3f)normal).y = -((Tuple3f)normal).y;
        } 
        ((Tuple3f)position).x = j;
        if (((Tuple3f)normal).z >= 0.0F) {
          if (this.colorSource == 0) {
            setFromRGB(diffuseColor, inPixels[index]);
          } else {
            setFromRGB(diffuseColor, this.material.diffuseColor);
          } 
          if (reflectivity != 0.0F && this.environmentMap != null) {
            this.tmpv2.set((Tuple3f)viewpoint);
            this.tmpv2.sub((Tuple3f)position);
            this.tmpv2.normalize();
            this.tmpv.set((Tuple3f)normal);
            this.tmpv.normalize();
            this.tmpv.scale(2.0F * this.tmpv.dot(this.tmpv2));
            this.tmpv.sub((Tuple3f)this.v);
            this.tmpv.normalize();
            setFromRGB(envColor, getEnvironmentMap(this.tmpv, inPixels, width, height));
            ((Tuple4f)diffuseColor).x = reflectivity * ((Tuple4f)envColor).x + areflectivity * ((Tuple4f)diffuseColor).x;
            ((Tuple4f)diffuseColor).y = reflectivity * ((Tuple4f)envColor).y + areflectivity * ((Tuple4f)diffuseColor).y;
            ((Tuple4f)diffuseColor).z = reflectivity * ((Tuple4f)envColor).z + areflectivity * ((Tuple4f)diffuseColor).z;
          } 
          Color4f c = phongShade(position, viewpoint, normal, diffuseColor, specularColor, this.material, lightsArray);
          int alpha = inPixels[index] & 0xFF000000;
          int rgb = (int)(((Tuple4f)c).x * 255.0F) << 16 | (int)(((Tuple4f)c).y * 255.0F) << 8 | (int)(((Tuple4f)c).z * 255.0F);
          outPixels[index++] = alpha | rgb;
        } else {
          outPixels[index++] = 0;
        } 
      } 
      float[] t = heightWindow[0];
      heightWindow[0] = heightWindow[1];
      heightWindow[1] = heightWindow[2];
      heightWindow[2] = t;
    } 
    return outPixels;
  }
  
  public Color4f phongShade(Vector3f position, Vector3f viewpoint, Vector3f normal, Color4f diffuseColor, Color4f specularColor, Material material, Light[] lightsArray) {
    this.shadedColor.set((Tuple4f)diffuseColor);
    this.shadedColor.scale(material.ambientIntensity);
    for (int i = 0; i < lightsArray.length; i++) {
      Light light = lightsArray[i];
      this.n.set((Tuple3f)normal);
      this.l.set((Tuple3f)light.position);
      if (light.type != 1)
        this.l.sub((Tuple3f)position); 
      this.l.normalize();
      float nDotL = this.n.dot(this.l);
      if (nDotL >= 0.0D) {
        float rv, dDotL = 0.0F;
        this.v.set((Tuple3f)viewpoint);
        this.v.sub((Tuple3f)position);
        this.v.normalize();
        if (light.type == 3) {
          dDotL = light.direction.dot(this.l);
          if (dDotL < light.cosConeAngle)
            continue; 
        } 
        this.n.scale(2.0F * nDotL);
        this.n.sub((Tuple3f)this.l);
        float rDotV = this.n.dot(this.v);
        if (rDotV < 0.0D) {
          rv = 0.0F;
        } else {
          rv = rDotV / (material.highlight - material.highlight * rDotV + rDotV);
        } 
        if (light.type == 3) {
          dDotL = light.cosConeAngle / dDotL;
          float e = dDotL;
          e *= e;
          e *= e;
          e *= e;
          e = (float)Math.pow(dDotL, (light.focus * 10.0F)) * (1.0F - e);
          rv *= e;
          nDotL *= e;
        } 
        this.diffuse_color.set((Tuple4f)diffuseColor);
        this.diffuse_color.scale(material.diffuseReflectivity);
        ((Tuple4f)this.diffuse_color).x *= ((Tuple4f)light.realColor).x * nDotL;
        ((Tuple4f)this.diffuse_color).y *= ((Tuple4f)light.realColor).y * nDotL;
        ((Tuple4f)this.diffuse_color).z *= ((Tuple4f)light.realColor).z * nDotL;
        this.specular_color.set((Tuple4f)specularColor);
        this.specular_color.scale(material.specularReflectivity);
        ((Tuple4f)this.specular_color).x *= ((Tuple4f)light.realColor).x * rv;
        ((Tuple4f)this.specular_color).y *= ((Tuple4f)light.realColor).y * rv;
        ((Tuple4f)this.specular_color).z *= ((Tuple4f)light.realColor).z * rv;
        this.diffuse_color.add((Tuple4f)this.specular_color);
        this.diffuse_color.clamp(0.0F, 1.0F);
        this.shadedColor.add((Tuple4f)this.diffuse_color);
      } 
      continue;
    } 
    this.shadedColor.clamp(0.0F, 1.0F);
    return this.shadedColor;
  }
  
  private int getEnvironmentMap(Vector3f normal, int[] inPixels, int width, int height) {
    if (this.environmentMap != null) {
      float angle = (float)Math.acos(-((Tuple3f)normal).y);
      float y = angle / 3.1415927F;
      if (y == 0.0F || y == 1.0F) {
        x = 0.0F;
      } else {
        float f = ((Tuple3f)normal).x / (float)Math.sin(angle);
        if (f > 1.0F) {
          f = 1.0F;
        } else if (f < -1.0F) {
          f = -1.0F;
        } 
        x = (float)Math.acos(f) / 3.1415927F;
      } 
      float x = ImageMath.clamp(x * this.envWidth, 0.0F, (this.envWidth - 1));
      y = ImageMath.clamp(y * this.envHeight, 0.0F, (this.envHeight - 1));
      int ix = (int)x;
      int iy = (int)y;
      float xWeight = x - ix;
      float yWeight = y - iy;
      int i = this.envWidth * iy + ix;
      int dx = (ix == this.envWidth - 1) ? 0 : 1;
      int dy = (iy == this.envHeight - 1) ? 0 : this.envWidth;
      return ImageMath.bilinearInterpolate(xWeight, yWeight, this.envPixels[i], this.envPixels[i + dx], this.envPixels[i + dy], this.envPixels[i + dx + dy]);
    } 
    return 0;
  }
  
  public String toString() {
    return "Stylize/Light Effects...";
  }
  
  public static class Material {
    float ambientIntensity = 0.5F;
    
    float diffuseReflectivity = 1.0F;
    
    float specularReflectivity = 1.0F;
    
    float highlight = 3.0F;
    
    float reflectivity = 0.0F;
    
    int diffuseColor = -7829368;
    
    int specularColor = -1;
    
    public void setDiffuseColor(int diffuseColor) {
      this.diffuseColor = diffuseColor;
    }
    
    public int getDiffuseColor() {
      return this.diffuseColor;
    }
  }
  
  public static class Light implements Cloneable {
    int type = 0;
    
    Vector3f position;
    
    Vector3f direction;
    
    Color4f realColor = new Color4f();
    
    int color = -1;
    
    float intensity;
    
    float azimuth;
    
    float elevation;
    
    float focus = 0.5F;
    
    float centreX = 0.5F, centreY = 0.5F;
    
    float coneAngle = 0.5235988F;
    
    float cosConeAngle;
    
    float distance = 100.0F;
    
    public Light() {
      this(4.712389F, 0.5235988F, 1.0F);
    }
    
    public Light(float azimuth, float elevation, float intensity) {
      this.azimuth = azimuth;
      this.elevation = elevation;
      this.intensity = intensity;
    }
    
    public void setAzimuth(float azimuth) {
      this.azimuth = azimuth;
    }
    
    public float getAzimuth() {
      return this.azimuth;
    }
    
    public void setElevation(float elevation) {
      this.elevation = elevation;
    }
    
    public float getElevation() {
      return this.elevation;
    }
    
    public void setDistance(float distance) {
      this.distance = distance;
    }
    
    public float getDistance() {
      return this.distance;
    }
    
    public void setIntensity(float intensity) {
      this.intensity = intensity;
    }
    
    public float getIntensity() {
      return this.intensity;
    }
    
    public void setConeAngle(float coneAngle) {
      this.coneAngle = coneAngle;
    }
    
    public float getConeAngle() {
      return this.coneAngle;
    }
    
    public void setFocus(float focus) {
      this.focus = focus;
    }
    
    public float getFocus() {
      return this.focus;
    }
    
    public void setColor(int color) {
      this.color = color;
    }
    
    public int getColor() {
      return this.color;
    }
    
    public void setCentreX(float x) {
      this.centreX = x;
    }
    
    public float getCentreX() {
      return this.centreX;
    }
    
    public void setCentreY(float y) {
      this.centreY = y;
    }
    
    public float getCentreY() {
      return this.centreY;
    }
    
    public void prepare(int width, int height) {
      float lx = (float)(Math.cos(this.azimuth) * Math.cos(this.elevation));
      float ly = (float)(Math.sin(this.azimuth) * Math.cos(this.elevation));
      float lz = (float)Math.sin(this.elevation);
      this.direction = new Vector3f(lx, ly, lz);
      this.direction.normalize();
      if (this.type != 1) {
        lx *= this.distance;
        ly *= this.distance;
        lz *= this.distance;
        lx += width * this.centreX;
        ly += height * this.centreY;
      } 
      this.position = new Vector3f(lx, ly, lz);
      this.realColor.set(new Color(this.color));
      this.realColor.scale(this.intensity);
      this.cosConeAngle = (float)Math.cos(this.coneAngle);
    }
    
    public Object clone() {
      try {
        Light copy = (Light)super.clone();
        return copy;
      } catch (CloneNotSupportedException e) {
        return null;
      } 
    }
    
    public String toString() {
      return "Light";
    }
  }
  
  public class AmbientLight extends Light {
    private final LightFilter this$0;
    
    public AmbientLight(LightFilter this$0) {
      this.this$0 = this$0;
    }
    
    public String toString() {
      return "Ambient Light";
    }
  }
  
  public class PointLight extends Light {
    private final LightFilter this$0;
    
    public PointLight(LightFilter this$0) {
      this.this$0 = this$0;
      this.type = 2;
    }
    
    public String toString() {
      return "Point Light";
    }
  }
  
  public class DistantLight extends Light {
    private final LightFilter this$0;
    
    public DistantLight(LightFilter this$0) {
      this.this$0 = this$0;
      this.type = 1;
    }
    
    public String toString() {
      return "Distant Light";
    }
  }
  
  public class SpotLight extends Light {
    private final LightFilter this$0;
    
    public SpotLight(LightFilter this$0) {
      this.this$0 = this$0;
      this.type = 3;
    }
    
    public String toString() {
      return "Spotlight";
    }
  }
}
