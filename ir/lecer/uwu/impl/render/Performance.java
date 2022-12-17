package ir.lecer.uwu.impl.render;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.interfaces.Setting;

public class Performance extends Module {
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Performance.enabled = enabled;
  }
  
  public static boolean enabled = false;
  
  public Performance() {
    super("Performance", Category.RENDER, true);
  }
  
  public void onSetup() {
    (Zypux.getInstance()).settingsManager.addSetting(new Setting("Smooth Fps", this, 100.0D, 30.0D, 300.0D, true));
    (Zypux.getInstance()).settingsManager.addSetting(new Setting("Unfocused Fps", this, 30.0D, 5.0D, 300.0D, true));
    (Zypux.getInstance()).settingsManager.addSetting(new Setting("Disable Rain/Snow", this, false));
  }
  
  public void onEnable() {
    setEnabled(true);
    super.onEnable();
  }
  
  public void onDisable() {
    super.onDisable();
    setEnabled(false);
  }
}
