package ir.lecer.uwu.ui.clickgui.elements.menu;

import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import java.awt.Color;
import net.minecraft.util.ResourceLocation;

public class ElementCheckBox extends Element {
  private final ResourceLocation tick = new ResourceLocation("zypux/textures/clickgui/tick.png");
  
  public ElementCheckBox(ModuleButton iparent, Setting iset) {
    this.parent = iparent;
    this.setting = iset;
    setup();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    FontUtils.raleway.drawString(this.elementTitle, this.x + 3.0D, this.y + (FontUtils.getFontHeight() / 2.0F) - 1.0D, (new Color(-1)).getRGB());
    RenderUtils.rect((int)(this.x + this.width - 15.0D), (int)(this.y + 3.0D), (int)(this.x + this.width - 3.0D), (int)(this.y + this.height - 3.0D), (new Color(-1291845632, true)).getRGB());
    RenderUtils.drawShadow((int)(this.x + this.width - 15.0D), (int)(this.y + 3.0D), 12.0F, 12.0F, 7, false);
    if (this.setting.isBooleanValue())
      RenderUtils.image(this.tick, (int)(this.x + this.width - 15.0D), (int)this.y, 16, 16, 255.0F); 
    if (isCheckHovered(mouseX, mouseY) && !this.setting.isBooleanValue())
      RenderUtils.image(this.tick, (int)(this.x + this.width - 15.0D), (int)this.y, 16, 16, 60.0F); 
  }
  
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
      this.setting.setBooleanValue(!this.setting.isBooleanValue());
      return true;
    } 
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  public boolean isCheckHovered(int mouseX, int mouseY) {
    return (mouseX >= this.x + this.width - 15.0D && mouseX <= this.x + this.width - 3.0D && mouseY >= this.y + 3.0D && mouseY <= this.y + this.height - 3.0D);
  }
}
