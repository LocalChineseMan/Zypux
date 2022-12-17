package ir.lecer.uwu.ui.clickgui.elements.menu;

import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;

public class ElementComboBox extends Element {
  public ElementComboBox(ModuleButton iparent, Setting iset) {
    this.parent = iparent;
    this.setting = iset;
    setup();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    String text = "> " + this.setting.getStringValue();
    RenderUtils.rect(this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(text) - 4.0D, this.y + 3.0D, this.x + this.width - 2.0D, this.y + this.onebox - 5.0D, (new Color(-1509949440, true)).getRGB());
    FontUtils.raleway.drawStringWithShadow(this.elementTitle, this.x + 3.0D, this.y + 2.0D, (new Color(-1, true)).getRGB());
    FontUtils.jetbrains_mono.drawString(text, this.x + this.width - FontUtils.jetbrains_mono.getStringWidth(text) - 2.0D, this.y + 1.0D, (new Color(-1, true)).getRGB());
    if (this.comboextended) {
      double ay = this.y + 15.0D;
      for (String string : this.setting.getOptions()) {
        if (string.equalsIgnoreCase(this.setting.getStringValue())) {
          FontUtils.jetbrains_mono.drawCenteredString(">", (int)(this.x + this.width / 2.0D) - FontUtils.jetbrains_mono.getStringWidth(string) / 2 - 10, (int)ay - 2, ColorUtils.getClickGUIBorderColor, true);
          FontUtils.jetbrains_mono.drawCenteredString("<", (int)(this.x + this.width / 2.0D) + FontUtils.jetbrains_mono.getStringWidth(string) / 2 + 10, (int)ay - 2, ColorUtils.getClickGUIBorderColor, true);
        } 
        FontUtils.jetbrains_mono.drawCenteredString(string, (int)(this.x + this.width / 2.0D), (int)ay - 2, (new Color(16777215)).getRGB(), false);
        ay += (FontUtils.getFontHeight() + 2);
      } 
    } 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0) {
      if (isButtonHovered(mouseX, mouseY)) {
        this.comboextended = !this.comboextended;
        return true;
      } 
      if (!this.comboextended)
        return false; 
      double ay = this.y + 15.0D;
      for (String slcd : this.setting.getOptions()) {
        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= ay && mouseY <= ay + FontUtils.getFontHeight() + 2.0D) {
          if (this.clickgui != null && this.clickgui.settingsManager != null)
            this.clickgui.settingsManager.getSettingByName(this.setting.getName()).setStringValue(slcd.toLowerCase()); 
          return true;
        } 
        ay += (FontUtils.getFontHeight() + 2);
      } 
    } 
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  public boolean isButtonHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + 15.0D);
  }
}
