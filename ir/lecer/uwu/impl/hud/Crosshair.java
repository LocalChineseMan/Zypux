package ir.lecer.uwu.impl.hud;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.interfaces.Module;

public class Crosshair extends Module {
  public static boolean enabled;
  
  public Crosshair() {
    super("Crosshair", Category.HUD, true);
  }
  
  public void onEnable() {
    enabled = true;
    super.onEnable();
  }
  
  public void onDisable() {
    super.onDisable();
    enabled = false;
  }
}
