package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

class BackupInfoList extends RealmsSimpleScrolledSelectionList {
  public BackupInfoList() {
    super(paramRealmsBackupInfoScreen.width(), paramRealmsBackupInfoScreen.height(), 32, paramRealmsBackupInfoScreen.height() - 64, 36);
  }
  
  public int getItemCount() {
    return (RealmsBackupInfoScreen.access$000(RealmsBackupInfoScreen.this)).changeList.size();
  }
  
  public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {}
  
  public boolean isSelectedItem(int item) {
    return false;
  }
  
  public int getMaxPosition() {
    return getItemCount() * 36;
  }
  
  public void renderBackground() {}
  
  protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
    String key = RealmsBackupInfoScreen.access$100(RealmsBackupInfoScreen.this).get(i);
    RealmsBackupInfoScreen.this.drawString(key, width() / 2 - 40, y, 10526880);
    String metadataValue = (String)(RealmsBackupInfoScreen.access$000(RealmsBackupInfoScreen.this)).changeList.get(key);
    RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.access$200(RealmsBackupInfoScreen.this, key, metadataValue), width() / 2 - 40, y + 12, 16777215);
  }
}
