package net.optifine.gui;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF extends GuiScreen {
  protected void actionPerformedRightClick(GuiButton button) {}
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    try {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 1) {
        GuiButton guibutton = getSelectedButton(mouseX, mouseY, this.buttonList);
        if (guibutton != null && guibutton.enabled) {
          guibutton.playPressSound(this.mc.getSoundHandler());
          actionPerformedRightClick(guibutton);
        } 
      } 
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public static GuiButton getSelectedButton(int x, int y, List<GuiButton> listButtons) {
    for (GuiButton guibutton : listButtons) {
      if (guibutton.visible) {
        int j = GuiVideoSettings.getButtonWidth(guibutton);
        int k = GuiVideoSettings.getButtonHeight(guibutton);
        if (x >= guibutton.xPosition && y >= guibutton.yPosition && x < guibutton.xPosition + j && y < guibutton.yPosition + k)
          return guibutton; 
      } 
    } 
    return null;
  }
}
