package ir.lecer.uwu.ui.clickgui.elements.menu;

import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;
import net.minecraft.util.MathHelper;

public class ElementColor extends Element {
  public boolean redDragging;
  
  public boolean greenDragging;
  
  public boolean blueDragging;
  
  public boolean alphaDragging;
  
  public ElementColor(ModuleButton iparent, Setting iset) {
    this.parent = iparent;
    this.setting = iset;
    setup();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    FontUtils.raleway.drawString(this.elementTitle, this.x + 3.0D, this.y + (FontUtils.getFontHeight() / 2.0F) - 4.0D, (new Color(-1)).getRGB());
    int newHeight = this.setting.isAlpha() ? 62 : 48;
    double oldHeight = this.height;
    this.height = newHeight;
    RenderUtils.rect(this.x, this.y + oldHeight, this.x + this.width + 1.0D, this.y + newHeight, (new Color(1493172224, true)).getRGB());
    String displayvalR = "Red: " + (Math.round(this.setting.getColor().getRed() * 100.0D) / 100.0D);
    FontUtils.jetbrains_mono.drawString(displayvalR, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(displayvalR), this.y, (new Color(-1, true)).getRGB());
    double percentBarR = (this.setting.getColor().getRed() / 255.0F);
    RenderUtils.rect(this.x + 2.0D, this.y + 13.0D, this.x + this.width - 2.0D, this.y + 15.0D, (new Color(-2147483648, true)).getRGB());
    RenderUtils.drawGradientHRect((float)this.x + 2.0F, (float)(this.y + 13.0D), (float)(this.x + percentBarR * this.width) - 2.0F, (float)(this.y + 15.0D), Color.RED.getRGB(), Color.RED.getRGB());
    int x1R = (int)(this.x + percentBarR * this.width - 2.0D);
    if (x1R < this.x + 2.0D)
      x1R = (int)(this.x + 3.0D); 
    RenderUtils.borderedCircle(x1R, (int)(this.y + 14.0D), 2.0F, Color.RED.getRGB(), Color.RED.getRGB());
    if (this.redDragging) {
      int val = (int)(MathHelper.clamp_double((mouseX - this.x) / this.width, 0.0D, 1.0D) * 255.0D);
      this.setting.setColor(new Color(val, this.setting.getColor().getGreen(), this.setting.getColor().getBlue(), this.setting.getColor().getAlpha()));
    } 
    int ig = 14;
    String displayvalG = "Green: " + (Math.round(this.setting.getColor().getGreen() * 100.0D) / 100.0D);
    FontUtils.jetbrains_mono.drawString(displayvalG, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(displayvalG), this.y + 14.0D, (new Color(-1, true)).getRGB());
    double percentBarG = (this.setting.getColor().getGreen() / 255.0F);
    RenderUtils.rect(this.x + 2.0D, this.y + 13.0D + 14.0D, this.x + this.width - 2.0D, this.y + 15.0D + 14.0D, (new Color(-2147483648, true)).getRGB());
    RenderUtils.drawGradientHRect((float)this.x + 2.0F, (float)(this.y + 13.0D) + 14.0F, (float)(this.x + percentBarG * this.width) - 2.0F, (float)(this.y + 15.0D) + 14.0F, Color.GREEN.getRGB(), Color.GREEN.getRGB());
    int x1G = (int)(this.x + percentBarG * this.width - 2.0D);
    if (x1G < this.x + 2.0D)
      x1G = (int)(this.x + 3.0D); 
    RenderUtils.borderedCircle(x1G, (int)(this.y + 14.0D) + 14, 2.0F, Color.GREEN.getRGB(), Color.GREEN.getRGB());
    if (this.greenDragging) {
      int val = (int)(MathHelper.clamp_double((mouseX - this.x) / this.width, 0.0D, 1.0D) * 255.0D);
      this.setting.setColor(new Color(this.setting.getColor().getRed(), val, this.setting.getColor().getBlue(), this.setting.getColor().getAlpha()));
    } 
    int ib = 29;
    String displayvalB = "Blue: " + (Math.round(this.setting.getColor().getBlue() * 100.0D) / 100.0D);
    FontUtils.jetbrains_mono.drawString(displayvalB, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(displayvalB), this.y + 29.0D, (new Color(-1, true)).getRGB());
    double percentBarB = (this.setting.getColor().getBlue() / 255.0F);
    RenderUtils.rect(this.x + 2.0D, this.y + 13.0D + 29.0D, this.x + this.width - 2.0D, this.y + 15.0D + 29.0D, (new Color(-2147483648, true)).getRGB());
    RenderUtils.drawGradientHRect((float)this.x + 2.0F, (float)(this.y + 13.0D) + 29.0F, (float)(this.x + percentBarB * this.width) - 2.0F, (float)(this.y + 15.0D) + 29.0F, Color.BLUE.getRGB(), Color.BLUE.getRGB());
    int x1B = (int)(this.x + percentBarB * this.width - 2.0D);
    if (x1B < this.x + 2.0D)
      x1B = (int)(this.x + 3.0D); 
    RenderUtils.borderedCircle(x1B, (int)(this.y + 14.0D) + 29, 2.0F, Color.BLUE.getRGB(), Color.BLUE.getRGB());
    if (this.blueDragging) {
      int val = (int)(MathHelper.clamp_double((mouseX - this.x) / this.width, 0.0D, 1.0D) * 255.0D);
      this.setting.setColor(new Color(this.setting.getColor().getRed(), this.setting.getColor().getGreen(), val, this.setting.getColor().getAlpha()));
    } 
    if (this.setting.isAlpha()) {
      int ia = 44;
      String displayvalA = "Alpha: " + (Math.round(this.setting.getColor().getAlpha() * 100.0D) / 100.0D);
      FontUtils.jetbrains_mono.drawString(displayvalA, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(displayvalA), this.y + 44.0D, (new Color(-1, true)).getRGB());
      double percentBarA = (this.setting.getColor().getAlpha() / 255.0F);
      RenderUtils.rect(this.x + 2.0D, this.y + 13.0D + 44.0D, this.x + this.width - 2.0D, this.y + 15.0D + 44.0D, (new Color(-2147483648, true)).getRGB());
      RenderUtils.drawGradientHRect((float)this.x + 2.0F, (float)(this.y + 13.0D) + 44.0F, (float)(this.x + percentBarA * this.width) - 2.0F, (float)(this.y + 15.0D) + 44.0F, (new Color(36095))
          .getRGB(), ColorUtils.getClickGUIBorderColor);
      int x1A = (int)(this.x + percentBarA * this.width - 2.0D);
      if (x1A < this.x + 2.0D)
        x1A = (int)(this.x + 3.0D); 
      RenderUtils.borderedCircle(x1A, (int)(this.y + 14.0D) + 44, 2.0F, ColorUtils.getClickGUIBorderColor, ColorUtils.getClickGUIBorderColor);
      if (this.alphaDragging) {
        int val = (int)(MathHelper.clamp_double((mouseX - this.x) / this.width, 0.0D, 1.0D) * 255.0D);
        this.setting.setColor(new Color(this.setting.getColor().getRed(), this.setting.getColor().getGreen(), this.setting.getColor().getBlue(), val));
      } 
    } 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isRedSliderHovered(mouseX, mouseY)) {
      this.redDragging = true;
      return true;
    } 
    if (mouseButton == 0 && isGreenSliderHovered(mouseX, mouseY)) {
      this.greenDragging = true;
      return true;
    } 
    if (mouseButton == 0 && isBlueSliderHovered(mouseX, mouseY)) {
      this.blueDragging = true;
      return true;
    } 
    if (mouseButton == 0 && isAlphaSliderHovered(mouseX, mouseY)) {
      this.alphaDragging = true;
      return true;
    } 
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  public void mouseReleased(int mouseX, int mouseY, int state) {
    this.redDragging = false;
    this.greenDragging = false;
    this.blueDragging = false;
    this.alphaDragging = false;
  }
  
  public boolean isRedSliderHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + 12.0D && mouseY <= this.y + 16.0D);
  }
  
  public boolean isGreenSliderHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + 12.0D + 14.0D && mouseY <= this.y + 16.0D + 14.0D);
  }
  
  public boolean isBlueSliderHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + 12.0D + 29.0D && mouseY <= this.y + 16.0D + 29.0D);
  }
  
  public boolean isAlphaSliderHovered(int mouseX, int mouseY) {
    if (!this.setting.isAlpha())
      return false; 
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + 12.0D + 44.0D && mouseY <= this.y + 16.0D + 44.0D);
  }
}
