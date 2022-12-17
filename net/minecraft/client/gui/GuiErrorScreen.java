package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
  private String field_146313_a;
  
  private String field_146312_f;
  
  public GuiErrorScreen(String p_i46319_1_, String p_i46319_2_) {
    this.field_146313_a = p_i46319_1_;
    this.field_146312_f = p_i46319_2_;
  }
  
  public void initGui() {
    super.initGui();
    this;
    this.buttonList.add(new GuiButton(0, width / 2 - 100, 140, I18n.format("gui.cancel", new Object[0])));
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this;
    this;
    drawGradientRect(0, 0, width, height, -12574688, -11530224);
    this;
    drawCenteredString(this.fontRendererObj, this.field_146313_a, width / 2, 90, 16777215);
    this;
    drawCenteredString(this.fontRendererObj, this.field_146312_f, width / 2, 110, 16777215);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {}
  
  protected void actionPerformed(GuiButton button) throws IOException {
    this.mc.displayGuiScreen((GuiScreen)null);
  }
}
