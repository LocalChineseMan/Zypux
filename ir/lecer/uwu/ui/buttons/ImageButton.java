package ir.lecer.uwu.ui.buttons;

import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ImageButton extends Gui {
  private ResourceLocation resourceLocation;
  
  private int x;
  
  private int y;
  
  private int width;
  
  private int height;
  
  private int id;
  
  private boolean animation;
  
  private int Xanimation;
  
  private int Yanimation;
  
  private String text;
  
  public void setResourceLocation(ResourceLocation resourceLocation) {
    this.resourceLocation = resourceLocation;
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public void setAnimation(boolean animation) {
    this.animation = animation;
  }
  
  public void setXanimation(int Xanimation) {
    this.Xanimation = Xanimation;
  }
  
  public void setYanimation(int Yanimation) {
    this.Yanimation = Yanimation;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public ImageButton(ResourceLocation resourceLocation, int x, int y, int width, int height, int id, boolean animation, int Xanimation, int Yanimation, String text) {
    this.resourceLocation = resourceLocation;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.id = id;
    this.animation = animation;
    this.Xanimation = Xanimation;
    this.Yanimation = Yanimation;
    this.text = text;
  }
  
  public ResourceLocation getResourceLocation() {
    return this.resourceLocation;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public int getId() {
    return this.id;
  }
  
  public boolean isAnimation() {
    return this.animation;
  }
  
  public int getXanimation() {
    return this.Xanimation;
  }
  
  public int getYanimation() {
    return this.Yanimation;
  }
  
  public String getText() {
    return this.text;
  }
  
  public void drawButton(int mouseX, int mouseY) {
    int animationX, animationY;
    if (isHovered(mouseX, mouseY) && isAnimation()) {
      animationX = this.x + this.Xanimation;
      animationY = this.y + this.Yanimation;
    } else {
      animationX = this.x;
      animationY = this.y;
    } 
    RenderUtils.image(this.resourceLocation, animationX, animationY, this.width, this.height, 255.0F);
    if (isHovered(mouseX, mouseY) && this.text != null) {
      int betterMouseX = mouseX + 10;
      RenderUtils.drawGradientHRect(betterMouseX, mouseY, (betterMouseX + FontUtils.comic.getStringWidth(this.text) + 6), (mouseY + 14), (new Color(-2147483648, true))
          .getRGB(), (new Color(35, 35, 35, 128))
          .getRGB());
      FontUtils.comic.drawStringWithShadow(this.text, (betterMouseX + 3), mouseY, (new Color(16777215)).getRGB());
    } 
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
  }
}
