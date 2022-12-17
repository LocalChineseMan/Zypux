package net.minecraft.client.gui;

import ir.lecer.uwu.features.Discord;
import ir.lecer.uwu.ui.buttons.ImageButton;
import ir.lecer.uwu.ui.menu.MainMenu;
import java.io.IOException;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
  public void initGui() {
    Discord.update("Idle", "");
    this.imageButtonList.clear();
    this.imageButtonList.add(new ImageButton(MainMenu.singleplayerIcon, 50, height / 2 - 175, 64, 64, 1, true, 5, 0, "Singleplayer"));
    this.imageButtonList.add(new ImageButton(MainMenu.multiplayerIcon, 50, height / 2 - 75, 64, 64, 2, true, 5, 0, "Multiplayer"));
    this.imageButtonList.add(new ImageButton(MainMenu.settingsIcon, 50, height / 2 + 25, 64, 64, 3, true, 5, 0, "Settings"));
    this.imageButtonList.add(new ImageButton(MainMenu.resourcePackIcon, 50, height / 2 + 125, 64, 64, 4, true, 5, 0, "Resource Pack"));
    this.imageButtonList.add(new ImageButton(MainMenu.quitIcon, width - 20, 5, 16, 16, 5, true, -1, 1, null));
    MainMenu.initGui();
  }
  
  protected void actionPerformed(ImageButton button) throws IOException {
    switch (button.getId()) {
      case 1:
        this.mc.displayGuiScreen((GuiScreen)new GuiSelectWorld(this));
        break;
      case 2:
        this.mc.displayGuiScreen(new GuiMultiplayer(this));
        break;
      case 3:
        this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        break;
      case 4:
        this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
        break;
      case 5:
        this.mc.shutdown();
        break;
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    MainMenu.drawScreen();
    MainMenu.mouseWheel();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
}
