package ir.lecer.uwu.ui.clickgui.elements.menu;

import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;
import net.minecraft.util.MathHelper;

public class ElementSlider extends Element {
  public boolean dragging;
  
  public ElementSlider(ModuleButton iparent, Setting iset) {
    this.parent = iparent;
    this.setting = iset;
    this.dragging = false;
    setup();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    String displayval = "" + (Math.round(this.setting.getDoubleValue() * 100.0D) / 100.0D);
    double percentBar = (this.setting.getDoubleValue() - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
    FontUtils.raleway.drawStringWithShadow(this.elementTitle, this.x + 3.0D, this.y, (new Color(-1, true)).getRGB());
    FontUtils.jetbrains_mono.drawString(displayval, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(displayval), this.y - 1.0D, (new Color(-1, true)).getRGB());
    RenderUtils.rect(this.x + 2.0D, this.y + 13.0D, this.x + this.width - 2.0D, this.y + 15.0D, (new Color(-2147483648, true)).getRGB());
    RenderUtils.drawGradientHRect((float)this.x + 2.0F, (float)(this.y + 13.0D), (float)(this.x + percentBar * this.width) - 2.0F, (float)(this.y + 15.0D), (new Color(36095))
        .getRGB(), ColorUtils.getClickGUIBorderColor);
    int x1 = (int)(this.x + percentBar * this.width - 2.0D);
    if (x1 < this.x + 2.0D)
      x1 = (int)(this.x + 3.0D); 
    RenderUtils.borderedCircle(x1, (int)(this.y + 14.0D), 2.0F, ColorUtils.getClickGUIBorderColor, ColorUtils.getClickGUIBorderColor);
    if (this.dragging) {
      double diff = this.setting.getMax() - this.setting.getMin();
      double val = this.setting.getMin() + MathHelper.clamp_double((mouseX - this.x) / this.width, 0.0D, 1.0D) * diff;
      this.setting.setDoubleValue(val);
    } 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isSliderHovered(mouseX, mouseY)) {
      this.dragging = true;
      return true;
    } 
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  public void mouseReleased(int mouseX, int mouseY, int state) {
    this.dragging = false;
  }
  
  public boolean isSliderHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y + 12.0D && mouseY <= this.y + 16.0D);
  }
}
