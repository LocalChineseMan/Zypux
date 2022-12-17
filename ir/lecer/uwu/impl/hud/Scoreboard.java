package ir.lecer.uwu.impl.hud;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.interfaces.Module;

public class Scoreboard extends Module {
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Scoreboard.enabled = enabled;
  }
  
  private static boolean enabled = false;
  
  public Scoreboard() {
    super("Scoreboard", Category.HUD, true);
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
