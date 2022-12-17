package ir.lecer.uwu.impl.movement;

import com.google.common.collect.Lists;
import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.UpdateEvent;
import ir.lecer.uwu.events.handler.EventHandler;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;
import ir.lecer.uwu.ui.clickgui.SettingsManager;
import java.util.ArrayList;

public class Sprint extends Module {
  private final ArrayList<String> mode = Lists.newArrayList((Object[])new String[] { "Normal", "NoPacket" });
  
  public Sprint() {
    super("Sprint", Category.MOVEMENT, true);
  }
  
  public void onSetup() {
    SettingsManager settingsManager = (Zypux.getInstance()).settingsManager;
    settingsManager.addSetting(new Setting("Bypass Mode", this, "Normal", this.mode));
    settingsManager.addSetting(new Setting("Speed", this, 5.0D, 1.0D, 10.0D, true));
    settingsManager.addSetting(new Setting("Multi Direction", this, true));
  }
  
  @EventHandler
  public void onUpdateEvent(UpdateEvent event) {
    String bypassMode = (Zypux.getInstance()).settingsManager.getSettingByName("Bypass Mode").getStringValue().toLowerCase();
    switch (bypassMode) {
      case "normal":
        this.mc.gameSettings.keyBindSprint.setPressed(true);
        break;
    } 
  }
}
