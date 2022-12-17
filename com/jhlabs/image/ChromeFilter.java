package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class ChromeFilter extends LightFilter {
  private float amount = 0.5F;
  
  private float exposure = 1.0F;
  
  public void setAmount(float amount) {
    this.amount = amount;
  }
  
  public float getAmount() {
    return this.amount;
  }
  
  public void setExposure(float exposure) {
    this.exposure = exposure;
  }
  
  public float getExposure() {
    return this.exposure;
  }
  
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    setColorSource(1);
    dst = super.filter(src, dst);
    TransferFilter tf = new TransferFilter(this) {
        private final ChromeFilter this$0;
        
        protected float transferFunction(float v) {
          v += this.this$0.amount * (float)Math.sin((v * 2.0F) * Math.PI);
          return 1.0F - (float)Math.exp((-v * this.this$0.exposure));
        }
      };
    return tf.filter(dst, dst);
  }
  
  public String toString() {
    return "Effects/Chrome...";
  }
}
