package ir.lecer.uwu.impl.render;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.interfaces.Module;

public class Blur extends Module {
  private static boolean enabled;
  
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Blur.enabled = enabled;
  }
  
  public Blur() {
    super("Blur", Category.RENDER, true);
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
