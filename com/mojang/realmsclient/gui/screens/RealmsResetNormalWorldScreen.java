package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsScreen;
import org.lwjgl.input.Keyboard;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
  private RealmsResetWorldScreen lastScreen;
  
  private RealmsEditBox seedEdit;
  
  private Boolean generateStructures = Boolean.valueOf(true);
  
  private Integer levelTypeIndex = Integer.valueOf(0);
  
  String[] levelTypes;
  
  private final int BUTTON_CANCEL_ID = 0;
  
  private final int BUTTON_RESET_ID = 1;
  
  private static final int BUTTON_LEVEL_TYPE_ID = 2;
  
  private static final int BUTTON_GENERATE_STRUCTURES_ID = 3;
  
  private final int SEED_EDIT_BOX = 4;
  
  private RealmsButton resetButton;
  
  private RealmsButton levelTypeButton;
  
  private RealmsButton generateStructuresButton;
  
  private String buttonTitle = getLocalizedString("mco.backup.button.reset");
  
  public RealmsResetNormalWorldScreen(RealmsResetWorldScreen lastScreen) {
    this.lastScreen = lastScreen;
  }
  
  public RealmsResetNormalWorldScreen(RealmsResetWorldScreen lastScreen, String buttonTitle) {
    this(lastScreen);
    this.buttonTitle = buttonTitle;
  }
  
  public void tick() {
    this.seedEdit.tick();
    super.tick();
  }
  
  public void init() {
    this
      
      .levelTypes = new String[] { getLocalizedString("generator.default"), getLocalizedString("generator.flat"), getLocalizedString("generator.largeBiomes"), getLocalizedString("generator.amplified") };
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    buttonsAdd(newButton(0, width() / 2 + 8, RealmsConstants.row(12), 97, 20, getLocalizedString("gui.back")));
    buttonsAdd(this.resetButton = newButton(1, width() / 2 - 102, RealmsConstants.row(12), 97, 20, this.buttonTitle));
    this.seedEdit = newEditBox(4, width() / 2 - 100, RealmsConstants.row(2), 200, 20);
    this.seedEdit.setFocus(true);
    this.seedEdit.setMaxLength(32);
    this.seedEdit.setValue("");
    buttonsAdd(this.levelTypeButton = newButton(2, width() / 2 - 102, RealmsConstants.row(4), 205, 20, levelTypeTitle()));
    buttonsAdd(this.generateStructuresButton = newButton(3, width() / 2 - 102, RealmsConstants.row(6) - 2, 205, 20, generateStructuresTitle()));
  }
  
  public void removed() {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void keyPressed(char ch, int eventKey) {
    this.seedEdit.keyPressed(ch, eventKey);
    if (eventKey == 28 || eventKey == 156)
      buttonClicked(this.resetButton); 
    if (eventKey == 1)
      Realms.setScreen(this.lastScreen); 
  }
  
  public void buttonClicked(RealmsButton button) {
    if (!button.active())
      return; 
    switch (button.id()) {
      case 0:
        Realms.setScreen(this.lastScreen);
        return;
      case 1:
        this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex.intValue(), this.generateStructures.booleanValue()));
        return;
      case 2:
        this.levelTypeIndex = Integer.valueOf((this.levelTypeIndex.intValue() + 1) % this.levelTypes.length);
        button.msg(levelTypeTitle());
        return;
      case 3:
        this.generateStructures = Boolean.valueOf(!this.generateStructures.booleanValue());
        button.msg(generateStructuresTitle());
        return;
    } 
  }
  
  public void mouseClicked(int x, int y, int buttonNum) {
    super.mouseClicked(x, y, buttonNum);
    this.seedEdit.mouseClicked(x, y, buttonNum);
  }
  
  public void render(int xm, int ym, float a) {
    renderBackground();
    drawCenteredString(getLocalizedString("mco.reset.world.generate"), width() / 2, 17, 16777215);
    drawString(getLocalizedString("mco.reset.world.seed"), width() / 2 - 100, RealmsConstants.row(1), 10526880);
    this.seedEdit.render();
    super.render(xm, ym, a);
  }
  
  private String levelTypeTitle() {
    String levelType = getLocalizedString("selectWorld.mapType");
    return levelType + " " + this.levelTypes[this.levelTypeIndex.intValue()];
  }
  
  private String generateStructuresTitle() {
    return getLocalizedString("selectWorld.mapFeatures") + " " + (this.generateStructures.booleanValue() ? getLocalizedString("mco.configure.world.on") : getLocalizedString("mco.configure.world.off"));
  }
}
