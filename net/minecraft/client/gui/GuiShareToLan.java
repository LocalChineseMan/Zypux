package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class GuiShareToLan extends GuiScreen {
  private final GuiScreen field_146598_a;
  
  private GuiButton field_146596_f;
  
  private GuiButton field_146597_g;
  
  private String field_146599_h = "survival";
  
  private boolean field_146600_i;
  
  public GuiShareToLan(GuiScreen p_i1055_1_) {
    this.field_146598_a = p_i1055_1_;
  }
  
  public void initGui() {
    this.buttonList.clear();
    this;
    this;
    this.buttonList.add(new GuiButton(101, width / 2 - 155, height - 28, 150, 20, I18n.format("lanServer.start", new Object[0])));
    this;
    this;
    this.buttonList.add(new GuiButton(102, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
    this;
    this.buttonList.add(this.field_146597_g = new GuiButton(104, width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode", new Object[0])));
    this;
    this.buttonList.add(this.field_146596_f = new GuiButton(103, width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands", new Object[0])));
    func_146595_g();
  }
  
  private void func_146595_g() {
    this.field_146597_g.displayString = I18n.format("selectWorld.gameMode", new Object[0]) + " " + I18n.format("selectWorld.gameMode." + this.field_146599_h, new Object[0]);
    this.field_146596_f.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + " ";
    if (this.field_146600_i) {
      this.field_146596_f.displayString += I18n.format("options.on", new Object[0]);
    } else {
      this.field_146596_f.displayString += I18n.format("options.off", new Object[0]);
    } 
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 102) {
      this.mc.displayGuiScreen(this.field_146598_a);
    } else if (button.id == 104) {
      if (this.field_146599_h.equals("spectator")) {
        this.field_146599_h = "creative";
      } else if (this.field_146599_h.equals("creative")) {
        this.field_146599_h = "adventure";
      } else if (this.field_146599_h.equals("adventure")) {
        this.field_146599_h = "survival";
      } else {
        this.field_146599_h = "spectator";
      } 
      func_146595_g();
    } else if (button.id == 103) {
      this.field_146600_i = !this.field_146600_i;
      func_146595_g();
    } else if (button.id == 101) {
      ChatComponentText chatComponentText;
      this.mc.displayGuiScreen((GuiScreen)null);
      String s = this.mc.getIntegratedServer().shareToLAN(WorldSettings.GameType.getByName(this.field_146599_h), this.field_146600_i);
      if (s != null) {
        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.publish.started", new Object[] { s });
      } else {
        chatComponentText = new ChatComponentText("commands.publish.failed");
      } 
      this.mc.ingameGUI.getChatGUI().printChatMessage((IChatComponent)chatComponentText);
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    this;
    drawCenteredString(this.fontRendererObj, I18n.format("lanServer.title", new Object[0]), width / 2, 50, 16777215);
    this;
    drawCenteredString(this.fontRendererObj, I18n.format("lanServer.otherPlayers", new Object[0]), width / 2, 82, 16777215);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
}
