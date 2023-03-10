package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class RealmsTermsScreen extends RealmsScreen {
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static final int BUTTON_AGREE_ID = 1;
  
  private static final int BUTTON_DISAGREE_ID = 2;
  
  private final RealmsScreen lastScreen;
  
  private final RealmsServer realmsServer;
  
  private RealmsButton agreeButton;
  
  private boolean onLink = false;
  
  private String realmsToSUrl = "https://minecraft.net/realms/terms";
  
  public RealmsTermsScreen(RealmsScreen lastScreen, RealmsServer realmsServer) {
    this.lastScreen = lastScreen;
    this.realmsServer = realmsServer;
  }
  
  public void init() {
    Keyboard.enableRepeatEvents(true);
    buttonsClear();
    int column1_x = width() / 4;
    int column_width = width() / 4 - 2;
    int column2_x = width() / 2 + 4;
    buttonsAdd(this.agreeButton = newButton(1, column1_x, RealmsConstants.row(12), column_width, 20, getLocalizedString("mco.terms.buttons.agree")));
    buttonsAdd(newButton(2, column2_x, RealmsConstants.row(12), column_width, 20, getLocalizedString("mco.terms.buttons.disagree")));
  }
  
  public void removed() {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void buttonClicked(RealmsButton button) {
    if (!button.active())
      return; 
    switch (button.id()) {
      case 2:
        Realms.setScreen(this.lastScreen);
        return;
      case 1:
        agreedToTos();
        return;
    } 
  }
  
  public void keyPressed(char eventCharacter, int eventKey) {
    if (eventKey == 1)
      Realms.setScreen(this.lastScreen); 
  }
  
  private void agreedToTos() {
    RealmsClient client = RealmsClient.createRealmsClient();
    try {
      client.agreeToTos();
      RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, (LongRunningTask)new RealmsTasks.RealmsConnectTask(this.lastScreen, this.realmsServer));
      longRunningMcoTaskScreen.start();
      Realms.setScreen(longRunningMcoTaskScreen);
    } catch (RealmsServiceException e) {
      LOGGER.error("Couldn't agree to TOS");
    } 
  }
  
  public void mouseClicked(int x, int y, int buttonNum) {
    super.mouseClicked(x, y, buttonNum);
    if (this.onLink) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(this.realmsToSUrl), null);
      RealmsUtil.browseTo(this.realmsToSUrl);
    } 
  }
  
  public void render(int xm, int ym, float a) {
    renderBackground();
    drawCenteredString(getLocalizedString("mco.terms.title"), width() / 2, 17, 16777215);
    drawString(getLocalizedString("mco.terms.sentence.1"), width() / 2 - 120, RealmsConstants.row(5), 16777215);
    int firstPartWidth = fontWidth(getLocalizedString("mco.terms.sentence.1"));
    int x1 = width() / 2 - 121 + firstPartWidth;
    int y1 = RealmsConstants.row(5);
    int x2 = x1 + fontWidth("mco.terms.sentence.2") + 1;
    int y2 = y1 + 1 + fontLineHeight();
    if (x1 <= xm && xm <= x2 && y1 <= ym && ym <= y2) {
      this.onLink = true;
      drawString(" " + getLocalizedString("mco.terms.sentence.2"), width() / 2 - 120 + firstPartWidth, RealmsConstants.row(5), 7107012);
    } else {
      this.onLink = false;
      drawString(" " + getLocalizedString("mco.terms.sentence.2"), width() / 2 - 120 + firstPartWidth, RealmsConstants.row(5), 3368635);
    } 
    super.render(xm, ym, a);
  }
}
