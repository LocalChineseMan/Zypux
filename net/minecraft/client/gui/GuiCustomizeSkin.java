package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenCapeOF;

public class GuiCustomizeSkin extends GuiScreen {
  private final GuiScreen parentScreen;
  
  private String title;
  
  public GuiCustomizeSkin(GuiScreen parentScreenIn) {
    this.parentScreen = parentScreenIn;
  }
  
  public void initGui() {
    int i = 0;
    this.title = I18n.format("options.skinCustomisation.title", new Object[0]);
    for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
      this;
      this;
      this.buttonList.add(new ButtonPart(enumplayermodelparts.getPartId(), width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
      i++;
    } 
    if (i % 2 == 1)
      i++; 
    this;
    this;
    this.buttonList.add(new GuiButtonOF(210, width / 2 - 100, height / 6 + 24 * (i >> 1), I18n.format("of.options.skinCustomisation.ofCape", new Object[0])));
    i += 2;
    this;
    this;
    this.buttonList.add(new GuiButton(200, width / 2 - 100, height / 6 + 24 * (i >> 1), I18n.format("gui.done", new Object[0])));
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.enabled) {
      if (button.id == 210)
        this.mc.displayGuiScreen((GuiScreen)new GuiScreenCapeOF(this)); 
      if (button.id == 200) {
        this.mc.gameSettings.saveOptions();
        this.mc.displayGuiScreen(this.parentScreen);
      } else if (button instanceof ButtonPart) {
        EnumPlayerModelParts enumplayermodelparts = ((ButtonPart)button).playerModelParts;
        this.mc.gameSettings.switchModelPartEnabled(enumplayermodelparts);
        button.displayString = func_175358_a(enumplayermodelparts);
      } 
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    this;
    drawCenteredString(this.fontRendererObj, this.title, width / 2, 20, 16777215);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  private String func_175358_a(EnumPlayerModelParts playerModelParts) {
    String s;
    if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
      s = I18n.format("options.on", new Object[0]);
    } else {
      s = I18n.format("options.off", new Object[0]);
    } 
    return playerModelParts.func_179326_d().getFormattedText() + ": " + s;
  }
  
  class ButtonPart extends GuiButton {
    private final EnumPlayerModelParts playerModelParts;
    
    private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts) {
      super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.func_175358_a(playerModelParts));
      this.playerModelParts = playerModelParts;
    }
  }
}
